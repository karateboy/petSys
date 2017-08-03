/**
 * Created by user on 2017/1/20.
 */
import axios from 'axios'
const emptyCustomer = {
    _id: 0,
    name:"",
    phone:"",
    petList: [],
    orderList: [],
    firstTime: 0,
    lastTime: 0
}

const state = {
    customer: JSON.parse(JSON.stringify(emptyCustomer)),
    isNewCustomer: true
}

const getters = {
    customer: state => {
        return state.customer
    },
    isNewCustomer: state => {
        return state.isNewCustomer
    }
}

const mutations = {
    assignCustomer: (state, payload) => {
        state.customer = payload.customer
        state.isNewCustomer = payload.isNewCustomer
    }
}

const actions = {
    emptyCustomer: ({commit}) => {
        const newCustomer = JSON.parse(JSON.stringify(emptyCustomer))
        commit('assignCustomer', {customer: newCustomer, isNewCustomer: true});
    },
    assignCustomer: ({commit}, payload) => {
        commit('assignCustomer', {customer: payload, isNewCustomer: false})
    },
    getCustomerByID:({commit}, id) => {
        const url = `/Customer/${id}`
        axios.get(url).then((resp)=>{
            const ret = resp.data
            commit('assignCustomer', {customer: ret, isNewCustomer: false})
        }).catch((err)=>{
            alert(err)
        })
        commit('assignCustomer', {customer: payload, isNewCustomer: false})
    },
}

export default {
    state,
    getters,
    mutations,
    actions
}
