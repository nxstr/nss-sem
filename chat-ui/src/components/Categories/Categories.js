import React from "react";
import Button from "@material-ui/core/Button";
import Input from "../Input";
import chatAPI from "../../services/chatapi";
import categoryAPI from "../../services/categoryapi";

const Categories = ({cats, user}) => {

    let handleUpdate = (event, id) => {
        console.log("here ", id)
        // onSubmitChat(id);

    }

    let onSendMessage = (msgText) => {
        categoryAPI.createCategory(user.username, msgText).then(res => {
            console.log('Sent', res);
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })
    }

    let renderCategory = (category) => {

        const {id, name} = category;
        console.log(category)
        return (
            <li className="Messages-list">
                <span
                    className="avatar"
                    style={{ backgroundColor: "yellow" }}
                />
                <div className="Message-content">
                    <div className="username">
                        {name}
                    </div>
                    <div className="text">{id}</div>
                </div>
                {/*<Button variant="contained" color="primary" onClick={event => handleDelete(event, id)} >*/}
                {/*    Delete*/}
                {/*</Button>*/}
                <Button variant="contained" color="primary" onClick={event => handleUpdate(event, id)} >
                    Update
                </Button>
            </li>
        );
    }

    return (
        <div className="App">

                <ul className="messages-list">
                    {cats.map(cat => renderCategory(cat))}
                </ul>
            <Input onSendMessage={onSendMessage}/>
        </div>
    )

}

export default Categories