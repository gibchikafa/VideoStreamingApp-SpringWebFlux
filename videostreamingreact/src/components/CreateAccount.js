import React, { Component } from 'react'
import Header from './layout/Header'

class CreateAccount extends Component{
    userData = {
        fullname: '',
        username: '',
        password: ''
    }



    constructor(props) {
        super(props);
        this.state = {
          user: this.userData,
          error: '',
          created : false
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let user = {...this.state.user};
        user[name] = value;
        this.setState({user});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {user, error, created} = this.state;

        try{
            const response = await fetch('/create', {
                method: 'POST',
                headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json'
                },
                body: JSON.stringify(user),
            });

            if(response.ok){
                this.setState({created: true})
            }
            else{
                await response.json().then(data => {
                    console.log(data);
                    this.setState({error: data.message})
                });
            }
        }
        catch(submitError){
            this.setState({error: submitError.message});
        }
        
    }

    render(){
        const {user, error, created} = this.state;

        return (
            <div className="App">
                <Header />
                <div className="container" style={containerStyle}>
                    {
                        created && (
                            <article className="message is-success">
                                <div className="message-header">
                                    <p>Success</p>
                                    <button className="delete" aria-label="delete"></button>
                                </div>
                                <div className="message-body">
                                    Your account has been created. Login with your details.
                                </div>
                            </article>
                        )
                    }
                    
                    {
                        !created && (
                            <div className="columns">
                                <div className={`column is-half`}>
                                    <nav className="panel">
                                        <p className="panel-heading">
                                            Fill in the form below to create account
                                        </p>
                                        <div className="panel-block">
                                            <form className="form" onSubmit={this.handleSubmit} style={formStyle} id="create-user-form">
                                                <p style={errorStyle}>{error !== '' ? 'Error: ' + error : ''}</p>
                                                <div className="field">
                                                    <label className="label">Fullname</label>
                                                    <div className="control">
                                                        <input className="input" value={user.fullname || ''} onChange={this.handleChange} id="fullname" autoComplete="fullname" name="fullname" type="text" placeholder="Enter fullname" required />
                                                    </div>
                                                </div>
                                                <div className="field">
                                                    <label className="label">Username</label>
                                                    <div className="control">
                                                        <input className="input" value={user.username || ''} onChange={this.handleChange} id="username" autoComplete="username" name="username" type="text" placeholder="Enter username" required />
                                                    </div>
                                                </div>
                                                <div className="field">
                                                    <label className="label">Password</label>
                                                    <div className="control">
                                                        <input className="input" value = {user.password || ''} onChange={this.handleChange} id="password" autoComplete="password" name="password" type="password" placeholder="Enter password" required />
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

export default CreateAccount;