import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import LiveStreams from './components/LiveStreams'
import CreateAccount from './components/CreateAccount'
import Login from './components/Login'
import CreateChannel from './components/CreateChannel';
import StreamingClient from './components/StreamingClient';
import StreamWatch from './components/StreamWatch';

class App extends Component {
  render(){
    return (
        <Router>
            <Switch>
              <Route path='/' exact={true} component={LiveStreams} />
              <Route path='/create-account' exact={true} component={CreateAccount} />
              <Route path='/login' exact={true} component={Login} />
              <Route path='/create-channel' exact={true} component={CreateChannel} />
              <Route path='/streamer/:uid/:cid' exact={true} component={StreamingClient} />
              <Route path='/channel/:uid/:cid' exact={true} component={StreamWatch} />
            </Switch>
        </Router>
    )
  }
}

export default App;
