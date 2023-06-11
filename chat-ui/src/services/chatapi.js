import Axios from "axios";

const api = Axios.create({
    baseURL: '/api/',
});

const chatAPI = {
    getMessages: (groupId, username) => {
        console.log('Calling get messages from API');
        return api.get(`${username}/chat/${groupId}`);
    },

    getChats:(username) =>{
      return api.get(`allChats/${username}`);
    },

    sendMessage: (username, text, chat) => {
        let msg = {
            messageType: "message",
            sender: username,
            content: text
        }
        console.log(chat.chatId)
        return api.post(`send/${chat.chatId}`, msg);
    },
    
    loginMessage: (username) => {
        console.log("here ")
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
    },
    logout: () => {
        return api.get(`logout`);
    }
}


export default chatAPI;
