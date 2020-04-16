import React, { Component } from 'react'
import Header from './layout/Header'
import AuthHeader from '../services/AuthHeader'

class CreateChannel extends Component {
    channelData = {
        description: ''
    }

    constructor(props) {
        super(props);
        this.state = {
            user: JSON.parse(localStorage.getItem("user")),
            error: '',
            channelInfo: this.channelData
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { user, error, channelInfo } = this.state;

        try {
            const toSubmit = {
                userId: user.id,
                description: channelInfo.description
            }

            const response = await fetch('/channels', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + user.jwt
                },
                body: JSON.stringify(toSubmit),
            });

            if (response.ok) {
                const headers = await response.headers;
                window.location = await "/streamer" + headers.get("Location");
            }
            else {
                await response.json().then(data => {
                    console.log(data);
                    this.setState({ error: data.message })
                });
            }
        }
        catch (submitError) {
            this.setState({ error: submitError.message });
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        let channelInfo = {...this.state.channelInfo}
        channelInfo[name] = value;
        this.setState({ channelInfo });
    }

    render() {
        const {user, error, channelInfo} = this.state;
        return (
            <div className="App">
                <Header />
                <div className="container" style={containerStyle}>
                    <div className="columns">
                        <div className={`column is-half`}>
                            <nav className="panel">
                                <p className="panel-heading">
                                    Fill in the form below to create a channel
                        </p>
                                <div className="panel-block">
                                    <form className="form" onSubmit={this.handleSubmit} style={formStyle} id="create-channel-form">
                                        <p style={errorStyle}>{error !== '' ? 'Error: ' + error : ''}</p>
                                        <div className="field">
                                            <label className="label">Description</label>
                                            <div className="control">
                                                <textarea className="textarea" name="description" id="description" placeholder="Write description" onChange={this.handleChange} value={channelInfo.description || ''}  required />
                                            </div>
                                        </div>
                                    </form>
                                </div>
                                <div className="panel-block">
                                    <button type="submit" form="create-channel-form" className={`button is-link is-outlined is-fullwidth`}>
                                        Submit
                            </button>
                                </div>
                            </nav>

                        </div>
                    </div>
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

export default CreateChannel;