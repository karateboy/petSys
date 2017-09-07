/**
 * Created by user on 2017/1/20.
 */
import axios from 'axios'
import { ICustomer } from '../../components/ICustomer'

const emptyCustomer: ICustomer = {
    _id: 0,
    name: "",
    phone: "",
    petList: [],
    orderList: [],
    firstTime: 0,
    lastTime: 0
}

interface IState {
    customer: ICustomer
    isNewCustomer: boolean
}

const state: IState = {
    customer: JSON.parse(JSON.stringify(emptyCustomer)),
    isNewCustomer: true
}

const getters = {
    customer: (state: IState) => {
        return state.customer
    },
    isNewCustomer: (state: IState) => {
        return state.isNewCustomer
    },
    isEmptyCustomer: (state: IState) => {
        if (state.customer._id == 0)
            return true
        else
            return false
    }
}

const mutations = {
    assignCustomer: (state: IState, payload: IState) => {
        state.customer = payload.customer
        state.isNewCustomer = payload.isNewCustomer
    }
}

const actions = {
    emptyCustomer: ({ commit }: { commit: any }) => {
        const newCustomer = JSON.parse(JSON.stringify(emptyCustomer))
        commit('assignCustomer', { customer: newCustomer, isNewCustomer: true });
    },
    assignCustomer: ({ commit }: { commit: any }, payload: ICustomer) => {
        commit('assignCustomer', { customer: payload, isNewCustomer: false })
    },
    getCustomerByID: ({ commit }: { commit: any }, id: number) => {
        const url = `/Customer/${id}`
        axios.get(url).then((resp) => {
            const ret = resp.data
            commit('assignCustomer', { customer: ret, isNewCustomer: false })
        }).catch((err) => {
            alert(err)
        })
    },
}

export default {
    state,
    getters,
    mutations,
    actions
}
