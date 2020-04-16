import React, { Component } from 'react'

export class LiveStream extends Component {
    render() {
        const { id, fullname, username, liveChannels } = this.props.streamingUser;
        const allUserStreams = liveChannels.map(channel => {
            return (
                
                    <div className={`column is-4`}>
                        <a href={"/channel/" + id + "/" + channel.id} key={channel.id}>
                            <div className="card">
                                <div className="content">
                                    <p className="subtitle is-4">{channel.description}</p>
                                    <time>Start-Time: {channel.startTime}</time>
                                </div>
                                <div className="card-content">
                                    <div className="media">
                                        <div className="media-content">
                                            <p className="subtitle is-5">
                                                {fullname}
                                            </p>
                                            <p className="subtitle is-6">
                                                @ {username}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </div>
            )
        });
        return (
            <React.Fragment>
                {allUserStreams}
            </React.Fragment>
        );
    }
}

export default LiveStream
