/**
 * Created by user on 2017/1/20.
 */
const emptyOrder = {
    _id: 0,
    customerID: 0,
    pet: "",
    storeID: 0,
    services: [],
    note: "",
    workers: [],
    time: 0,
    active: true
}

const state = {
    isEmptyOrder: true,
    order: JSON.parse(JSON.stringify(emptyOrder))
}

const getters = {
    order: state => {
        return state.order;
    },
    isEmptyOrder: state => {
        return state.isEmptyOrder
    }
}

const mutations = {
    assignOrder: (state, payload) => {
        state.order = payload.order
        state.isEmptyOrder = payload.isEmptyOrder
    }
}

const actions = {
    newOrder: ({commit}) => {
        const newOrder = JSON.parse(JSON.stringify(emptyOrder))
        commit('assignOrder', {order: newOrder, isEmptyOrder: true});
    },
    newOrderWithCustomer: ({commit}, payload) => {
        const newOrder = JSON.parse(JSON.stringify(emptyOrder))
        newOrder.customerID = payload.customer._id
        if (payload.customer.petList.length > 0){
            newOrder.pet = payload.customer.petList[0].name
        }
        else
            newOrder.pet = "-"

        if(payload.storeList.length > 0){
            newOrder.storeID = payload.storeList[0]._id
        }
        commit('assignOrder', {order: newOrder, isEmptyOrder: true});
    },
    assignOrder: ({commit}, myOrder) => {
        commit('assignOrder', {order: myOrder, isEmptyOrder: false})
    }
}

export default {
    state,
    getters,
    mutations,
    actions
}
