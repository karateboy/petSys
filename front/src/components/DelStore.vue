<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">店鋪:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="store in storeList" @click="storeID=store._id">
                            <input type="radio">{{ store.name }} </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="delStore">刪除</button>
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
import IStore from './IStore'
import IUser from './IUser'

@Component
export default class DelStore extends Vue {
    storeID: ""
    @Getter('user') user: IUser
    @Getter('storeList') storeList: Array<IStore>
    mounted() {
        this.refreshStoreList()
    }
    @Action('refreshStoreList') refreshStoreList: () => void
    delStore() {
        const url = `/Store/${this.storeID}`
        axios.delete(url).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert('成功')
                this.refreshStoreList()
            } else
                alert('失敗')
        })
    }
}
</script>
