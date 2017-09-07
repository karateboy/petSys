/**
 * Created by user on 2017/1/20.
 */
import IOrder from '../../components/IOrder'
import { ICustomer } from '../../components/ICustomer'
import IStore from '../../components/IStore'
const emptyOrder: IOrder = {
    _id: 0,
    customerID: 0,
    pet: {
        name: "",
        breed: ""
    },
    storeID: 0,
    services: [],
    note: "",
    workers: [],
    time: 0,
    active: true
}
interface IState {
    isEmptyOrder: boolean
    order: IOrder
}

const state: IState = {
    isEmptyOrder: true,
    order: JSON.parse(JSON.stringify(emptyOrder))
}

const getters = {
    order: (state: IState) => {
        return state.order;
    },
    isEmptyOrder: (state: IState) => {
        return state.isEmptyOrder
    }
}

const mutations = {
    assignOrder: (state: IState, payload: {
        order: IOrder
        isEmptyOrder: boolean
    }) => {
        state.order = payload.order
        state.isEmptyOrder = payload.isEmptyOrder
    }
}

const actions = {
    newOrder: ({ commit }: { commit: any }) => {
        const newOrder = JSON.parse(JSON.stringify(emptyOrder))
        commit('assignOrder', { order: newOrder, isEmptyOrder: true });
    },
    newOrderWithCustomer: ({ commit }: { commit: any }, payload: {
        customer: ICustomer
        storeList: Array<IStore>
    }) => {
        const newOrder = JSON.parse(JSON.stringify(emptyOrder))
        newOrder.customerID = payload.customer._id
        if (payload.customer.petList.length > 0) {
            newOrder.pet = payload.customer.petList[0].name
        }
        else
            newOrder.pet = "-"

        if (payload.storeList.length > 0) {
            newOrder.storeID = payload.storeList[0]._id
        }
        commit('assignOrder', { order: newOrder, isEmptyOrder: true });
    },
    assignOrder: ({ commit }: { commit: any }, myOrder: IOrder) => {
        commit('assignOrder', { order: myOrder, isEmptyOrder: false })
    }
}

export default {
    state,
    getters,
    mutations,
    actions
}
