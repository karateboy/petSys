/**
 * Created by user on 2017/1/12.
 */
import axios from 'axios';

const state = {
    authenticated: false,
    user: {},
    storeList: []
}

const getters = {
    isAuthenticated: state =>{
        return state.authenticated;
    },
    user: state =>{
        return state.user;
    },
    storeList: state =>{
        return state.storeList
    }
}

const mutations = {
    updateAuthenticated: (state, payload) => {
        state.authenticated = payload.authenticated
        state.user = JSON.parse(JSON.stringify(payload.user))
        state.storeList.splice(0, state.storeList.length)
        for(let store of payload.storeList){
            console.log(store)
            state.storeList.push(store)
        }
        console.log(state.storeList)
    },
    updateStoreList: (state, payload)=>{
        state.storeList.splice(0, state.storeList.length)
        for(let store of payload){
            state.storeList.push(store)
        }
    }
}

const actions = {
    logout : ({commit}) => {
        commit('updateAuthenticated', {authenticated:false, user:{}});
    },
    refreshStoreList: ({commit})=>{
        console.log("refreshStoreList")
        axios.get("/Store").then((resp)=>{
            const ret = resp.data
            console.log(ret)
            commit('updateStoreList', ret)
        }).catch((err)=>{
            alert(err)
        })
    }
}

export default {
    state,
    getters,
    mutations,
    actions
}
