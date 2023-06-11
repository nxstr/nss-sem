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

        const {id, playerUsername} = chat;
        console.log(chat)
        return (
            <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                <div className="Message-content">
                    <div className="username">
                        {playerUsername}
                    </div>
                    <div className="text">{id}</div>
                </div>
                <Button variant="contained" color="primary" onClick={event => handleSubmit(event, id, playerUsername)} >
                    Open
                </Button>
            </li>
        );
    }

    return (
        <div className="App">
            {!!chat ?
                (
                    <>
                {/*    <Messages*/}
                {/*        messages={messages}*/}
                {/*        currentUser={currentUser}*/}
                {/*    />*/}
                {/*<Input onSendMessage={onSendMessage} />*/}
                        </>
                ) :
                <ul className="messages-list">
                    {chats.map(chat => renderChat(chat))}
                </ul>
            }
        </div>
    )

}

export default AllChats