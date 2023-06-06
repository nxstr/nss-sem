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
    }

}

export default categoryAPI;