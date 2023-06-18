import Axios from "axios";

const api = Axios.create({
    baseURL: '/api/',
});

const categoryAPI = {
    createCategory: (username, name) =>{
        let msg = {
            name: name
        }
        return api.post(`${username}/categories/new`, msg);
    },
    getCats: (username) => {
        return api.get(`${username}/categories`);
    },
    deleteCategory:(username, id) => {
        return api.delete(`${username}/categories/delete/${id}`);
    },
    updateCategory:(username, id, name) => {
        let msg = {
            id: id,
            name: name
        }
        return api.put(`${username}/categories/update/${id}`, msg);
    },
    login:(username, password) => {
        let msg = {
            username: username,
            password:password
        }
        console.log(password, "password")
        return api.post(`login`, msg);
    }

}

export default categoryAPI;