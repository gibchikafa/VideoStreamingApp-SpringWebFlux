import React, { Component } from 'react'
import Header from './layout/Header'

class StreamingClient extends Component {
    constructor(props) {
        super(props)
        this.state = {
            streamingClient: null,
            cid: '',
            uid: '',
            mediaSourceSupported: true
        };

    }

    initializeStreaming(cid, uid) {
        let constraintObj = {
            audio: false,
            video: {
                facingMode: "user",
                width: { min: 640, ideal: 640 },
                height: { min: 400, ideal: 400 }
            }
        };

        //handle older browsers that might implement getUserMedia in some way
        if (navigator.mediaDevices === undefined) {
            navigator.mediaDevices = {};
            navigator.mediaDevices.getUserMedia = function (constraintObj) {
                let getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
                if (!getUserMedia) {
                    this.setState({ mediaSourceSupported: false });
                    return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
                }
                return new Promise(function (resolve, reject) {
                    getUserMedia.call(navigator, constraintObj, resolve, reject);
                });
            }
        }
        else {
            navigator.mediaDevices.enumerateDevices()
                .then(devices => {
                    devices.forEach(device => {
                        console.log(device.kind.toUpperCase(), device.label);
                    })
                })
                .catch(err => {
                    console.log(err.name, err.message);
                })
        }

        navigator.mediaDevices.getUserMedia(constraintObj)
            .then(function (mediaStreamObj) {
                const clientWebSocket = new WebSocket("ws://localhost:8080/streamer/" + uid + "/" + cid);

                clientWebSocket.onclose = function (error) {
                    console.log("clientWebSocket.onclose", clientWebSocket, error);
                }

                clientWebSocket.onerror = function (error) {
                    console.log("clientWebSocket.onerror", clientWebSocket, error);
                }

                clientWebSocket.onmessage = function (message) {

                }

                //connect the media stream to the first video element
                let video = document.querySelector('video');
                if ("srcObject" in video) {
                    video.srcObject = mediaStreamObj;
                } else {
                    //old version
                    video.src = window.URL.createObjectURL(mediaStreamObj);
                }

                video.onloadedmetadata = function (ev) {
                    //show in the video element what is being captured by the webcam
                    video.play();
                };

                //add listeners for saving video/audio
                let start = document.getElementById('btnStart');
                let stop = document.getElementById('btnStop');

                let mediaRecorder = new MediaRecorder(mediaStreamObj);

                start.addEventListener('click', (ev) => {
                    mediaRecorder.start(100);
                })

                stop.addEventListener('click', (ev) => {
                    mediaRecorder.stop();
                });

                mediaRecorder.ondataavailable = function (ev) {
                    sendToServer(clientWebSocket, ev.data);
                }

                mediaRecorder.onstop = (ev) => {

                }

                let sendToServer = (clientWebSocket, blobData) => {
                    if (blobData.size > 65536) {
                        let blobChunks = sliceFile(blobData, 65000);
                        let i = 0;
                        while (i < blobChunks.length) {
                            clientWebSocket.send(blobChunks[i]);
                            i++;
                        }
                    }
                    else if (blobData.size > 0) {
                        clientWebSocket.send(blobData);
                    }
                }

                let sliceFile = (blobData, chunksSize) => {
                    let byteIndex = 0;
                    let blobChunks = [];

                    while (byteIndex < blobData.size) {
                        blobChunks.push(blobData.slice(byteIndex, byteIndex + chunksSize));
                        byteIndex += chunksSize;

                    }

                    return blobChunks;
                }

            })
            .catch(function (err) {
                console.log(err.name, err.message);
            });
    }

    async componentDidMount() {
        if (this.props.match.params.uid !== '' && this.props.match.params.cid) {
            const uid = this.props.match.params.uid;
            const cid = this.props.match.params.cid;

            const response = await fetch(`/channel-info/${uid}/${cid}`);
            const channelInfo = await response.json();

            this.setState({ streamingClient: channelInfo, cid: cid, uid: uid });

            this.initializeStreaming(cid, uid);
        }
    }

    render() {
        const { streamingClient, mediaSourceSupported } = this.state;
        return (
            <div className="App">
                <Header />
                <div className="container" style={containerStyle}>
                    {
                        streamingClient != null && mediaSourceSupported && (
                            <React.Fragment>
                                {
                                    streamingClient.liveChannels.length > 0 && (
                                        <div className="columns is-multiline">
                                            <div className="column is-12">
                                                <h3 className="subtitle is-3">Streaming: {streamingClient.liveChannels[0].description}</h3>
                                            </div>
                                            <div className="column is-12">
                                                <div className="buttons">
                                                    <button className={`button is-primary`} id="btnStart">
                                                        <strong>Start Streaming</strong>
                                                    </button>
                                                    <button className={`button is-light`} id="btnStop">
                                                        <strong>Stop Streaming</strong>
                                                    </button>
                                                </div>
                                            </div>
                                            <div className="column is-12">
                                                <video id="vid" controls autoPlay>

                                                </video>
                                            </div>
                                        </div>
                                    )
                                }
                                {
                                    streamingClient.liveChannels.length == 0 && (
                                        <h3 className="subtitle is-3" title-color="red">This channel has been removed. Please create another channel</h3>
                                    )
                                }
                            </React.Fragment>

                        )
                    }
                    {
                        streamingClient == null && (
                            <h3 className="subtitle is-3" title-color="red">Failed to get channelInfo</h3>
                        )
                    }

                    {
                        !mediaSourceSupported && (
                            <h3 className="subtitle is-3" title-color="red">Error: Media Source Not Supported</h3>
                        )
                    }

                </div>
            </div>
        )
    }
}

const formStyle = {
    width: '100%',
}

const containerStyle = {
    marginTop: '20px'
}

const errorStyle = {
    color: 'red'
}

export default StreamingClient;
