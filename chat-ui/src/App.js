import React, { useState } from 'react';
import SockJsClient from 'react-stomp';
import './App.css';
import Input from './components/Input/Input';
import LoginForm from './components/LoginForm';
import Messages from './components/Messages/Messages';
import chatAPI from './services/chatapi';
import { randomColor } from './utils/common';
import AllChats from "./components/AllChats";
import Chat from "./components/Chat";
import {
  BrowserRouter as Router, Switch,
  Route, Redirect, BrowserRouter, Routes, useNavigate,
} from "react-router-dom";


const SOCKET_URL = 'http://localhost:8080/ws-chat/';

const App = () => {
  const [messages, setMessages] = useState([])
  const [chats, setChats] = useState([])
  const [user, setUser] = useState(null)
  const [activeChat, setActiveChat] = useState(null)


  let onConnected = () => {
    console.log("Connected!!")
    console.log(user.username, " username")
    chatAPI.loginMessage(user.username).then(res => {
      console.log('Sent login', res);
    }).catch(err => {
      console.log('Error Occured while sending message to api', err);
    })
    getChats(user);
  }

  let getChats = (user) =>{
    chatAPI.getChats(user.username).then(res=> {
      setChats(chats.concat(res.data));
    }).catch(err => {
      console.log('Error Occured while getting chats to api');
    })

  }

  let onMessageReceived = (msg) => {
    console.log('New Message Received!!', msg);
    setMessages(messages.concat(msg));
    console.log(messages, " messages")
  }

  let onSendMessage = (msgText) => {
    chatAPI.sendMessage(user.username, msgText, activeChat).then(res => {
      console.log('Sent', res);
    }).catch(err => {
      console.log('Error Occured while sending message to api');
    })
  }
  
    let onLoginMessage = (username) => {
    chatAPI.loginMessage(user.username).then(res => {
      console.log('Sent login', res);
    }).catch(err => {
      console.log('Error Occured while sending message to api');
    })
  }

  let handleLoginSubmit = (users) => {
    var pieces = users.split(" ");
    console.log(pieces[0], " Logged in..");

    setUser({
      username: pieces[0],
      color: randomColor()
    })

  }

  let handleChatId = (id) =>{
    setActiveChat({
      chatId: id
    })
    chatAPI.getMessages(id, user.username).then(res =>{
      console.log(res.data)
      setMessages(messages.concat(res.data))
    }).catch(err => {
      console.log('Error Occured while getting messages to api');
    })
  }

  let getChat = (user) => {
    if (user) {
      console.log(" user is " + user.username)
      return user.username
    } return ""
  }

  return (
    <div className="App">
      {!!user ?
        (
          <>
            <SockJsClient
              url={SOCKET_URL}
              topics={[`/topic/group/${getChat(user)}`]}
              onConnect={onConnected}
              onDisconnect={console.log("Disconnected!")}
              onMessage={
                msg => onMessageReceived(msg)
              }
              debug={false}

            />
            {/*<Messages*/}
            {/*  messages={messages}*/}
            {/*  currentUser={user}*/}
            {/*/>*/}
            {/*<Input onSendMessage={onSendMessage} />*/}
            {!!activeChat?(
                <>
                    <Messages
                  messages={messages}
              currentUser={user}
            />
            <Input onSendMessage={onSendMessage} />
                </>
            ) :
                <AllChats
                    chats = {chats}
                    currentUser = {user}
                    onSubmitChat = {handleChatId}
                    // onSubmit = {openChat(id)}
                />
            }

          {/* тут буде не інпут а якийсь getAllChatsForUser, який буде брати всі чати через гет запрос з контроллера,
           після того юзер буде клікати на чат, і має відкритись юрл з чат айді, тут же getFllMessagesForChat який спрацьовує зразу після кліку,
           після чого вже буде оцей Input component*/}
          </>
        ) :
        <LoginForm onSubmit={handleLoginSubmit} />
      }
    </div>
  )
}

export default App;
