<template>
    <div>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">公司</label>
                <div class="col-lg-5">
                    <input type="text" placeholder="帳號" class="form-control" v-model="user.company" :readonly="!isCompanyOwner">
                </div>
            </div>

            <div class="form-group">
                <label class="col-lg-2 control-label">登入帳號</label>
                <div class="col-lg-5">
                    <input type="text" placeholder="帳號" class="form-control" required v-model="user.name" :readonly="!isNew">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">密碼</label>
                <div class="col-lg-5">
                    <input type="password" placeholder="密碼" class="form-control" required v-model="user.password">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">密碼再輸入</label>
                <div class="col-lg-5">
                    <input type="password" class="form-control" required v-model="passwordRetype">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">e-mail</label>
                <div class="col-lg-5">
                    <input type="email" class="form-control" required v-model="user.email">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">行動電話</label>
                <div class="col-lg-5">
                    <input name='phone' type="tel" placeholder="電話號碼" class="form-control" required v-model="user.phone">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">群組:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="group in groupInfoList" @click="user.groupID=group.id" :class="{active: user.groupID==group.id }">
                            <input type="radio">{{ group.name }} </label>
                    </div>
                </div>
            </div>
            <div class="form-group" v-if="storeList.length > 0">
                <label class="col-lg-2 control-label">店鋪:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="store in storeList" @click="toggleStoreList(store._id)" :class="{active: containStore(store._id) }">
                            <input type="checkbox">{{ store.name }} </label>
                    </div>
                </div>
            </div>

            <div class="form-group" v-if='isNew'>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="newUser">新增</button>
                </div>
            </div>
            <div class="form-group" v-else>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="updateUser">更新</button>
                </div>
            </div>
        </div>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
import axios from 'axios'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import IUser from './IUser'
import GroupInfo from './GroupInfo'
import IStore from './IStore'

@Component
export default class User extends Vue {
    @Prop({
        type: Object,
        required: true
    }) user: IUser
    @Prop({
        type: Boolean,
        required: true
    }) isNew: boolean
    @Prop() registerCompany: Boolean

    mounted() {
        axios.get('/GroupInfo').then((resp) => {
            const ret = resp.data
            this.groupInfoList.splice(0, this.groupInfoList.length)
            for (let group of ret) {
                if (this.registerCompany) {
                    if (group.id == 'Owner')
                        this.groupInfoList.push(group)
                } else
                    this.groupInfoList.push(group)
            }
        }).catch((err) => {
            alert(err)
        })
    }

    groupInfoList: Array<GroupInfo> = []
    passwordRetype: string = ""

    @Getter('storeList') storeList: Array<IStore>
    get passwordDifferent() {
        return this.user.password != this.passwordRetype
    }

    @Action('logout') logout: () => void
    containStore(storeID: number) {
        return this.user.storeList.indexOf(storeID) != -1
    }

    toggleStoreList(storeID: number) {
        let index = this.user.storeList.indexOf(storeID)
        if (index > -1) {
            this.user.storeList.splice(index, 1)
        } else {
            this.user.storeList.push(storeID)
        }
    }

    isCompanyOwner() {
        if (this.user.groupID == "Owner")
            return true
        else return false
    }

    newUser() {
        if (this.user.password != this.passwordRetype) {
            alert('密碼不一致')
            return
        }

        if (this.isCompanyOwner()) {
            if (!this.user.company || this.user.company.length == 0) {
                alert("公司名稱不能是空的")
                return
            }
        }
        let url
        if (this.user.groupID == 'Owner')
            url = '/Owner'
        else
            url = '/User'
        axios.post(url, this.user).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("成功")

                if (this.user.groupID == 'Owner') {
                    this.$router.push({ name: 'Login' })
                }
            } else
                alert("失敗:" + ret.msg)
        }).catch((err) => {
            alert(err)
        })
    }

    updateUser() {
        console.log(this.user)
        if (this.user.password != this.passwordRetype) {
            alert('密碼不一致')
            return
        }

        const url = `/User/${encodeURIComponent(this.user._id)}`
        axios.put(url, this.user).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("成功")
                this.$emit('userUpdated')
            }
            else
                alert("失敗:" + ret.msg)
        }).catch((err) => {
            alert(err)
        })
    }
}
</script>
