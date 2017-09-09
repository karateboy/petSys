/**
 * Created by user on 2017/1/12.
 */
import vue from 'vue'
import axios from 'axios';
import IUser from '../../components/IUser'
import { emptyUser } from '../../components/IUser'
import IStore from '../../components/IStore'

interface IState {
    authenticated: boolean,
    user: IUser,
    storeList: Array<IStore>
}

const state: IState = {
    authenticated: false,
    storeList: [],
    user: emptyUser
}

const getters = {
    isAuthenticated: (state: IState) => {
        return state.authenticated;
    },
    user: (state: IState) => {
        return state.user;
    },
    storeList: (state: IState) => {
        return state.storeList
    },
    companyOwner: (state: IState) => {
        if (state.user)
            return state.user.groupID === 'Owner'
        else
            return false
    },
    companyAdmin: (state: IState) => {
        if (state.user)
            return state.user.groupID === 'Manager' || state.user.groupID === 'Owner'
        else
            return false
    },
    companyClerk: (state: IState) => {
        if (state.user)
            return state.user.groupID === 'Clerk'
        else
            return false
    },
    companyUser: (state: IState) => {
        if (state.user)
            return state.user.groupID === 'Manager' || state.user.groupID === 'Owner' || state.user.groupID === 'Clerk'
        else
            return false
    },
    sysAdmin: (state: IState) => {
        if (state.user)
            return state.user.groupID === 'Admin'
        else
            return false
    }

}

const mutations = {
    updateAuthenticated: (state: IState,
        payload: {
            authenticated: boolean
            user: IUser
            storeList: Array<IStore>
        }) => {
        state.authenticated = payload.authenticated
        state.user = JSON.parse(JSON.stringify(payload.user))
        state.storeList.splice(0, state.storeList.length)
        for (let store of payload.storeList) {
            state.storeList.push(store)
        }
    },
    updateStoreList: (state: IState, payload: Array<IStore>) => {
        state.storeList.splice(0, state.storeList.length)
        for (let store of payload) {
            state.storeList.push(store)
        }
    }
}

const actions = {
    logout: ({ commit }: { commit: any }) => {
        commit('updateAuthenticated', { authenticated: false, user: {}, storeList: [] });
    },
    refreshStoreList: ({ commit }: { commit: any }) => {
        axios.get("/Store").then((resp) => {
            const ret = resp.data
            commit('updateStoreList', ret)
        }).catch((err) => {
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
