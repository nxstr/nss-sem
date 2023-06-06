import React from 'react'
import Button from "@material-ui/core/Button";
import {yellow} from "@material-ui/core/colors";

const Messages = ({ messages, currentUser, chats, onSubmitChat }) => {

    let renderMessage = (message) => {
        const { sender, content, color } = message;
        // const messageFromMe = currentUser.username === message.sender;
        let messageFromMe = false;
        if(currentUser.username!==message.chat && message.sender!==message.chat){
            messageFromMe = true;
        }else if(currentUser.username===message.chat && currentUser.username===message.sender){
            messageFromMe = true;
        }
        const className = messageFromMe ? "Messages-message currentUser" : "Messages-message";
        return (
            <li className={className}>
                <span
                    className="avatar"
                    style={{ backgroundColor: color }}
                />
                <div className="Message-content">
                    <div className="username">
                        {sender}
                    </div>
                    <div className="text">{content}</div>
                </div>
            </li>
        );
    };

    let handleSubmit = (event, id) => {
        // window.location.replace('api/allChats');
        console.log("here ", id)
        onSubmitChat(id);
        // setChat({
        //     chatId: id
        // })
        // chatAPI.getMessages(id).then(res =>{
        //     console.log(res.data)
        //     setMessages(messages.concat(res.data))
        // }).catch(err => {
        //     console.log('Error Occured while getting messages to api');
        // })
    }

    let renderChat = (chat) => {

        const {id, playerUsername} = chat;
        console.log(chat)
        return (
            <li className="Chats-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: yellow }}
                />
                <div className="Message-content">
                    <div className="username">
                        {playerUsername}
                    </div>
                    <div className="text">{id}</div>
                </div>
                <Button variant="contained" color="primary" onClick={event => handleSubmit(event, id)} >
                    Open
                </Button>
            </li>
        );
    }

    return (
        <div>
            <ul className="chats-list">
                {chats.map(m=>renderChat(m))}
            </ul>
            <ul className="messages-list">
                {messages.map(msg => renderMessage(msg))}
            </ul>
        </div>
    )
}


export default Messages