import React, { Component } from 'react'
import Header from './layout/Header'

class StreamWatch extends Component {
    constructor(props) {
        super(props);

        this.state = {
            channelInfo: null,
            cid: '',
            uid: '',
            error: ''
        }
    }

    initializeStreaming(cid) {
        let videoDisplay = document.getElementById('vid');
        let sourceBuffer = null;
        let mediaSource = null;
        let chunks = [];
        let sourceBufferReady = false;

        let setDefaults = () => {
            if (window.MediaSource) {
                initialiseSources().then(sourceBufferState => {
                    connectToSocket();
                });
            }
            else {
                this.setState({ error: "Media source not supported!" });
            }

            
        }

        let initialiseSources = async () => {
            let promise = new Promise(async (resolve, reject) => {
                mediaSource = new MediaSource();
                videoDisplay.src = await URL.createObjectURL(mediaSource);
                resolve(await mediaSource.addEventListener('sourceopen', (e) => {
                    URL.revokeObjectURL(videoDisplay.src);
                    //let mediaSource = e.target;
                    sourceBuffer = e.target.addSourceBuffer('video/webm;codecs=vp8');

                }));
            });

            return promise;
        }

        let connectToSocket = () => {
            const clientWebSocket = new WebSocket("ws://localhost:8080/client/" + cid);

            clientWebSocket.onclose = (error) => {
                console.log("clientWebSocket.onclose", clientWebSocket, error);
            }

            clientWebSocket.onerror = (error) => {
                console.log("clientWebSocket.onerror", clientWebSocket, error);
            }

            clientWebSocket.onmessage = async (message) => {
                
                await processMessage(message).then(async (vidData) => {
                    await updateSourceBuffer(vidData);
                });
            }

            let processMessage = async (message) => {
                let promise = await new Promise((resolve, reject) => {
                    let fileReader = new FileReader();
                    fileReader.onload = function (event) {
                        if (new Uint8Array(event.target.result).length > 0) {
                            resolve(new Uint8Array(event.target.result));
                        }
                    };
                    fileReader.readAsArrayBuffer(message.data);
                });

                return promise;

            }

            let updateSourceBuffer = async (vidData) => {
                let promise = await new Promise(async (resolve, reject) => {
                    if (sourceBuffer != null) {
                        if(!sourceBuffer.updating){
                            resolve(await sourceBuffer.appendBuffer(vidData));
                        }
                    }
                });
                return promise;
            }
        }

        setDefaults();
    }

    async componentDidMount() {
        if (this.props.match.params.uid !== '' && this.props.match.params.cid) {
            const uid = this.props.match.params.uid;
            const cid = this.props.match.params.cid;

            const response = await fetch(`/channel-info/${uid}/${cid}`);
            const channelInfo = await response.json();

            this.setState({ channelInfo: channelInfo, cid: cid, uid: uid });

            if(channelInfo != null){
                if(channelInfo.liveChannels.length > 0){
                    this.initializeStreaming(cid);
                }
            }
            
        }
    }

    render() {
        const { channelInfo, uid, cid, error } = this.state;

        return (
            <div className="App">
                <Header />
                <div className="container" style={containerStyle}>
                    {
                        channelInfo != null && error == '' && (
                            <div className="columns is-multiline">
                                {
                                    channelInfo.liveChannels.length > 0 && (
                                        <React.Fragment>
                                            <div className="column is-12">
                                                <h3 className="subtitle is-3">Watching: {channelInfo.liveChannels[0].description}</h3>
                                            </div>
                                            <div className="column is-12">
                                                <video id="vid" autoPlay controls>

                                                </video>
                                            </div>
                                        </React.Fragment>
                                    )
                                }
                                {
                                    channelInfo.liveChannels.length == 0 && (
                                        <React.Fragment>
                                            <div className="columns is-multiline">
                                                <div className="column is-12">
                                                    <h3 className="subtitle is-3">Failed to get channel information. The user might have deleted the channel</h3>
                                                </div>
                                            </div>
                                        </React.Fragment>
                                    )
                                }
                            </div>
                        )
                    }

                    {
                        channelInfo == null && (
                            <div className="columns is-multiline">
                                <div className="column is-12">
                                    <h3 className="subtitle is-3">Failed to get channel information. The user might have deleted the channel</h3>
                                </div>
                            </div>
                        )
                    }

                    {
                        error != "" && (
                            <div className="columns is-multiline">
                                <div className="column is-12">
                                    <h3 className="subtitle is-3">Error: {error}</h3>
                                </div>
                            </div>
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

export default StreamWatch;