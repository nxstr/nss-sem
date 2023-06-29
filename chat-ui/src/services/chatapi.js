import Axios from "axios";

const api = Axios.create({
    baseURL: 'http://localhost:3000/api/',
});

const chatAPI = {
    getMessages: (groupId, username) => {
        return api.get(`${username}/chat/${groupId}`);
    },

    getChats:(username) =>{
      return api.get(`allChats/${username}`);
    },

    sendMessage: (username, text, chat, cats) => {
        let msg = {
            messageType: "message",
            sender: username,
            content: text,
            categories: cats
        }
        return api.post(`send/${chat.chatId}`, msg);
    },
    
    loginMessage: (username) => {
        let msg = {
            messageType: "message",
            sender: username,
            content: "logged in",
            chat: ""
        }
        return api.post(`log`, msg);
    },
    createChat: () => {
        return api.post(`chats/new`);
    },
    registerPlayer: (username, password, email) => {
        let msg = {
            username: username,
            password: password,
            email: email
        }
        return api.post(`register/player`, msg);
        // return api.post(`reg/emp`);
    },
    logout: () => {
        return api.get(`logout`);
    },
    getCurrentEmployee:() => {
        return api.get(`employee/current`);
    },
    getCurrentPlayer:() => {
        return api.get(`player/current`);
    },
    updateCurrent:(username, email, password, id, type) => {

        let dto = {
            "username": username,
            "password": password,
            "email": email,
            "id": id
        }
        if(type==="employee"){
            return api.put(`employee/current/edit`, dto);
        }else{
            return api.put(`player/current/edit`, dto);
        }

    },
    closeChat:(id)=>{
        return api.put(`chats/${id}/close`);
    },
    openChat:(id)=>{
        return api.put(`chats/${id}/open`);
    },
    saveCategories:(id, list) => {
        return api.put(`chats/${id}/cats`, list);
    },
    getChat:(id) => {
        return api.get(`chats/${id}/get`);
    }
}


export default chatAPI;
