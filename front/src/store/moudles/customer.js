/**
 * Created by user on 2017/1/20.
 */
const emptyCustomer = {
    _id: 0,
    petList: [],
    orderList: [],
    firstTime: 0,
    lastTime: 0
}

const state = {
    customer: JSON.parse(JSON.stringify( emptyCustomer))
}

const getters = {
    customer: state =>{
        return state.customer;
    },
}

const mutations = {
    assignCustomer: (state, payload) => {
        state.customer = payload
    }
}

const actions = {
    newCustomer : ({commit}) => {
        const newCustomer = JSON.parse(JSON.stringify( emptyCustomer))
        commit('assignCustomer', newCustomer);
    }
}

export default {
    state,
    getters,
    mutations,
    actions
}
