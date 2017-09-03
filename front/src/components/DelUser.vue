<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">使用者:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="user in userList" @click="userID=user._id">
                            <input type="radio">{{ user.name }} </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="delUser">刪除</button>
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

export default class DelUser extends Vue {
    userList: Array<IUser>
    userID: ""
    @Getter('user') user: IUser
    mounted() {
        this.refresh()
    }

    delUser() {
        const url = `/User/${encodeURIComponent(this.userID)}`
        console.log(url)
        axios.delete(url).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert('成功')
                this.refresh()
            } else
                alert('失敗')
        })
    }
    refresh() {
        const url = `/User/${this.user.company}/0/1000`
        axios.get(url).then((resp) => {
            const ret = resp.data
            this.userList.splice(0, this.userList.length)
            for (let user of ret) {
                this.userList.push(user)
            }
        }).catch((err) => {
            alert(err)
        })
    }
    components: {}
}
</script>
