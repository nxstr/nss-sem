import React, {useEffect, useRef, useState} from 'react'
import Button from '@material-ui/core/Button';
import chatAPI from "../services/chatapi";

const AllChats = ({chats, currentUser, onSubmitChat})=>{
    const [messages, setMessages] = useState([])
    const [chat ,setChat] = useState(null)

    // console.log(chats)

    let onSendMessage = (msgText) => {
        chatAPI.sendMessage(currentUser.username, msgText, 'bbbb4').then(res => {
            console.log('Sent', res);
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })
    }

    let handleSubmit = (event, id, name) => {
        onSubmitChat(id, name);
    }

    let renderChat = (chat) => {

        const {id, playerUsername, lastMessage} = chat;
        const d = new Date(lastMessage.date);
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
                    <div>{d.getHours()}:{d.getMinutes()}, {d.getDate()}.{d.getMonth()}.{d.getFullYear()}</div>
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