import React, {useState} from "react";
import Button from "@material-ui/core/Button";
import Input from "../Input";
import chatAPI from "../../services/chatapi";
import categoryAPI from "../../services/categoryapi";

const Categories = ({cats, user, onSendMessageCat}) => {

    const [uid, setUid] = useState(null);
    let getUpdateName = (name) => {
        categoryAPI.updateCategory(user.username, uid, name).then(res => {
            if(res.status===200){
                setUid(null);
                onSendMessageCat();
            }
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })
    }

    let handleUpdate = (event, id) => {
        console.log("here ", id)
        setUid(id);
        // return (
        //     <div>
        //         <Input onSendMessage={getUpdateName}/>
        //     </div>
        //     );

    }



    let handleDelete = (event, id) => {
        console.log("here ", id)
        // onSubmitChat(id);
        categoryAPI.deleteCategory(user.username, id).then(res => {
            if(res.status===200){
                onSendMessageCat();
            }
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })

    }


    let onSendMess = (msgText) => {
        // onSendMessageCat(msgText);


        categoryAPI.createCategory(user.username, msgText).then(res => {
            console.log('Sent', res);
            if(res.status===201){
                onSendMessageCat();
            }
        }).catch(err => {
            console.log('Error Occured while sending message to api');
        })
    }
    // let onSendMessage = (msgText) => {
    //     categoryAPI.createCategory(user.username, msgText).then(res => {
    //         console.log('Sent', res);
    //         if(res.status===201){
    //             categoryAPI.getCats(user.username).then(res => {
    //                 cats = res.data;
    //
    //             })
    //         }
    //     }).catch(err => {
    //         console.log('Error Occured while sending message to api');
    //     })
    // }

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
                <Button variant="contained" color="primary" onClick={event => handleDelete(event, id)} >
                    Delete
                </Button>
                <Button variant="contained" color="primary" onClick={event => handleUpdate(event, id)} >
                    Update
                </Button>
                {!!uid && uid===id && (
                    <div>
                        <Input onSendMessage={getUpdateName}/>
                    </div>
                )}
            </li>
        );
    }

    return (
        <div className="cats">

                <ul className="chat-list">
                    {cats.map(cat => renderCategory(cat))}
                </ul>
            <Input onSendMessage={onSendMess}/>
        </div>
    )

}

export default Categories