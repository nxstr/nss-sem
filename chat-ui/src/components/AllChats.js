import React, { useState } from 'react'
import Button from '@material-ui/core/Button';
import chatAPI from "../services/chatapi";

const AllChats = ({chats, currentUser, onSubmitChat})=>{
    console.log("allchats", chats)
    const [messages, setMessages] = useState([])
    const [chat ,setChat] = useState(null)

    let onMessageReceived = (msg) => {
        console.log('New Message Received!!', msg);
        setMessages(messages.concat(msg));
    }

    // console.log(chats)

    let onSendMessage = (msgText) => {
        chatAPI.sendMessage(currentUser.username, msgText, 'bbbb4').then(res => {
            console.log('Sent', res);
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })
    }

    let handleSubmit = (event, id, name) => {
        // window.location.replace('api/allChats');
        console.log("here ", id)
        onSubmitChat(id, name);
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

        const {id, playerUsername, lastMessage} = chat;
        console.log(chat)
        return (
            <li className="chat-box">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                <div className="box-content">
                    <div className="chat-username">
                        {playerUsername}
                    </div>
                    <div className="chat-last-sender">
                        {lastMessage.sender} :
                    </div>
                    <div className="chat-text">{lastMessage.content}</div>
                </div>
                <Button onClick={event => handleSubmit(event, id, playerUsername)} className="chat-box-button">
                    Open
                </Button>
            </li>
        );
    }

    return (
        <div className="chats">
                <ul className="chat-list">
                    {chats.map(chat => renderChat(chat))}
                </ul>
        </div>
    )

}

export default AllChats