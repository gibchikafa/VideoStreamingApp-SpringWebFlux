import React from 'react'

class AuthService{
    async login(username, password){
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json'
            },
            body: JSON.stringify({username:username, password:password})
        });

        return await response.json();
    }

    logout() {
        localStorage.removeItem("user");
        window.location = "/";
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));;
    }
}

export default new AuthService();