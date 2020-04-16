import React, { Component } from 'react'
import Header from './layout/Header'
import AuthService from '../services/AuthService'

class Login extends Component {
    userData = {
        username: '',
        password: ''
    }

    constructor(props) {
        super(props);
        this.state = {
            user: this.userData,
            error: ''
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let user = { ...this.state.user };
        user[name] = value;
        this.setState({ user });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { user, error } = this.state;

        await AuthService.login(user.username, user.password).then(response => {
            if (response.jwt) {
                localStorage.setItem("user", JSON.stringify(response));
                window.location = "/";
            }
            else {
                this.setState({ error: response.message });
            }
        });
    }


    render() {
        const { user, error } = this.state;

        return (
            <div className="App">
                <Header />
                <div className="container" style={containerStyle}>
                    <div className="columns">
                        <div className={`column is-half`}>
                            <nav className="panel">
                                <p className="panel-heading">
                                    Fill in the form below login
                            </p>
                                <div className="panel-block">
                                    <form className="form" onSubmit={this.handleSubmit} style={formStyle} id="create-user-form">
                                        <p style={errorStyle}>{error !== '' ? 'Error: ' + error : ''}</p>
                                        <div className="field">
                                            <label className="label">Username</label>
                                            <div className="control">
                                                <input className="input" value={user.username || ''} onChange={this.handleChange} id="username" autoComplete="username" name="username" type="text" placeholder="Enter username" required />
                                            </div>
                                        </div>
                                        <div className="field">
                                            <label className="label">Password</label>
                                            <div className="control">
                                                <input className="input" value={user.password || ''} onChange={this.handleChange} id="password" autoComplete="password" name="password" type="password" placeholder="Enter password" required />
                                            </div>
                                        </div>
                                    </form>
                                </div>
                                <div className="panel-block">
                                    <button type="submit" form="create-user-form" className={`button is-link is-outlined is-fullwidth`}>
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

export default Login;