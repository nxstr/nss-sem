import React, {useEffect, useRef} from 'react'
import Button from "@material-ui/core/Button";
import {yellow} from "@material-ui/core/colors";

const Messages = ({ messages, currentUser, chats, onSubmitChat }) => {
    const bottomRef = useRef(null);

    useEffect(() => {bottomRef.current.scrollIntoView();
    }, [messages]);

    let renderMessage = (message) => {
        const { sender, content, color, date } = message;
        const d = new Date(date);
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
                <div className="Message-content">
                    <div className="username">
                        {sender}
                    </div>
                    <div className="text">{content}</div>
                    <div className="date">{d.getHours()}:{d.getMinutes()}, {d.getDate()}.{d.getMonth()}.{d.getFullYear()}</div>
                </div>
            </li>
        );
    };



    let handleSubmit = (event, id, name) => {
        onSubmitChat(id, name);
    }

    let renderChat = (chat) => {
        const {id, playerUsername, lastMessage} = chat;
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
                    <div className="text">{lastMessage.sender} : {lastMessage.content}</div>
                </div>
                <Button variant="contained" color="primary" onClick={event => handleSubmit(event, id, playerUsername)} >
                    Open
                </Button>
            </li>
        );
    }

    return (<div className="messagecomp">
            {/*<ul className="chats-list">*/}
            {/*    {chats.map(m=>renderChat(m))}*/}
            {/*</ul>*/}
            <ul className="messages-list">
                {messages.map(msg => renderMessage(msg))}
                <li ref={bottomRef}/>
            </ul>

        </div>
    )
}


export default Messages