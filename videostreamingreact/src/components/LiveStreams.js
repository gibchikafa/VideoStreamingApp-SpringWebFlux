import React, { Component } from 'react'
import LiveStream from './LiveStream'
import Header from './layout/Header'


class LiveStreams extends Component {

    state = {
        isLoading:true,
        streamingUsers : []
    }
    
    async componentDidMount(){
        const response = await fetch("/channels");
        await response.json().then(data => {
            this.setState({streamingUsers: data, isLoading:false});
        })
    }

    render(){
        const {streamingUsers, isLoading} = this.state;
        
        const streamsList = streamingUsers.map(user => {
            return <LiveStream key={user.id} streamingUser ={user}/>
        });

        if(isLoading){
            return (
                <p>Loading...</p>
            )
        }

        return (
            <div className="App">
                <Header />
                <div className="container">
                    <p className="subtitle is-3">All Channels</p>
                    <div className="columns is-multiline">
                        <React.Fragment>
                            {streamsList}
                        </React.Fragment>
                    </div>
                    
                </div>
            </div>
        )
    }

}


export default LiveStreams;