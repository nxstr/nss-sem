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
            sender: username,
            content: text
        }
        console.log(chat.chatId)
        // if(username==="bbb" || username==="ccc"){
        //     return api.post(`send/employee/aaa`, msg);
        // }
        return api.post(`send/${chat.chatId}`, msg);
    },
    
    loginMessage: (username) => {
        console.log("here ")
        let msg = {
            sender: username,
            content: "logged in",
            chat: ""
        }
        return api.post(`log`, msg);
    }
}


export default chatAPI;
