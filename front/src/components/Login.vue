<template>
    <div>
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-offset-2 col-lg-6">
                    <form class="form-horizontal">
                        <fieldset>
                            <div class="form-group">
                                <label class="col-lg-2 control-label">公司:</label>
                                <div class="col-lg-6"><input class="form-control" placeholder="帳號" type="email" v-model="credential.company" required autofocus>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-lg-2 control-label">帳號:</label>
                                <div class="col-lg-6"><input class="form-control" placeholder="帳號" type="email" v-model="credential.name" required autofocus>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-lg-2 control-label">密碼:</label>
                                <div class="col-lg-6"><input class="form-control" placeholder="密碼" type="password" required v-model="credential.password">
                                </div>
                            </div>
                            <!-- Change this to a button or input when using this as a form -->
                            <div class="form-group">
                                <div class="col-lg-offset-2 col-lg-3">
                                    <button type="submit" class="btn btn-lg btn-success btn-block" :disabled="!ready" @click.prevent="login">登入
                                    </button>
                                </div>
                                <div class="col-lg-3">
                                    <button class="btn btn-lg btn-success btn-block" @click.prevent="registerCompany">註冊公司
                                    </button>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>

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

@Component
export default class Login extends Vue {
    mounted() {
        axios.get("/testAuthenticated").then(
            (resp) => {
                const ret = resp.data
                if (ret.ok) {
                    const user = ret.user
                    const storeList = ret.storeList
                    this.$store.commit('updateAuthenticated', { authenticated: true, user, storeList });
                    this.$router.push({ name: 'Dashboard' })
                }
            }
        ).catch((err) => {
            // simply ignore it...
        })
    }

    credential = {
        company: "",
        name: "",
        password: ""
    }

    get ready() {

        if (this.credential.name != "" && this.credential.password != "")
            return true;
        else
            return false;
    }

    @Action('refreshStoreList') refreshStoreList: () => void
    login() {
        const url = "/authenticate"
        const resultP = axios.post(url, this.credential)

        resultP.then(
            (resp) => {
                const ret = resp.data

                if (ret.ok) {
                    const user = ret.user
                    const storeList = ret.storeList
                    this.$store.commit('updateAuthenticated', { authenticated: true, user, storeList });
                    //this.$store.commit('updateStoreList', ret.storeList)
                    this.$router.push({ name: 'Dashboard' })
                } else {
                    alert(ret.msg)
                }
            }
        ).catch(
            (err) => {
                alert(err)
            }
            )
    }
    registerCompany() {
        console.log("register Company")
        this.$router.push({ name: 'RegisterCompany' })
    }

}
</script>
