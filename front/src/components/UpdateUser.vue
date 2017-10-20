<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">使用者:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="user in userList" @click="selectedUser=user">
                            <input type="radio">{{ user.name }} </label>
                    </div>
                </div>
            </div>
        </div>
        <div v-if='selectedUser'>
            <user :user='selectedUser' :isNew='false' @userUpdated="refresh()"></user>
        </div>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
import User from './User.vue'
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

@Component({
    components: {
        User: User
    }
})
export default class UpdateUser extends Vue {
    userList: IUser[] = []
    selectedUser: IUser|null = null
    mounted() {
        this.refresh()
    }

    @Getter('user') user: IUser
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
}
</script>
