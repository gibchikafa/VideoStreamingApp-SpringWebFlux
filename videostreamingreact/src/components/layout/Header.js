import React, { Component } from 'react'
import AuthService from '../../services/AuthService'

const NavbarItem = props => (
    <a className="navbar-item" href="/">
        <span>VideoStreaming</span>
    </a>
);

const NavbarBurger = props => (
    <a role="button" aria-label="menu" aria-expanded="false" data-target="app-menu" className={`navbar-burger burger`}>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
    </a>
)


class Header extends Component {
    constructor(props) {
        super(props);
        this.state = {
            user: JSON.parse(localStorage.getItem('user'))
        };
    }

    
    render() {
        const { user } = this.state;
        return (
            <nav className={`navbar  is-dark`}>
                <div className="container">
                    <div className="navbar-brand">
                        <NavbarItem />
                        <NavbarBurger />
                    </div>
                    <div id="app-menu" className="navbar-menu">
                        <div className="navbar-start">
                            <a className="navbar-item" href="/">
                                Home
                            </a>
                            {
                                user != null && (
                                    <a className="navbar-item" href="/create-channel">
                                        Create channel
                                    </a>
                                )
                            }
                        </div>
                    </div>

                    <div className="navbar-end">
                        <div className="navbar-item">
                            {
                                user != null && (
                                    <div className="buttons">
                                        <button className="button is-white">{user.fullname + "@" + user.username}</button>
                                        <button className="button is-info" onClick={AuthService.logout}>Logout</button>
                                    </div>
                                    
                                )
                            }
                            {
                                user == null && (
                                    <div className="buttons">
                                        <a className={`button is-primary`} href="/create-account">
                                            <strong>Create Account</strong>
                                        </a>
                                        <a className={`button is-light`} href="/login">
                                            <strong>Login</strong>
                                        </a>
                                    </div>
                                )
                            }

                        </div>
                    </div>
                </div>

            </nav>
        )
    }

}


const userLabelStyle = {
    color: 'white !important'
}

export default Header;