<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">店鋪:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="store in storeList" @click="selectedStore=store">
                            <input type="radio">{{ store.name }} </label>
                    </div>
                </div>
            </div>
        </div>
        <div v-if='selectedStore'>
            <store :store='cloneSelectedStore()' :isNew='false'></store>
        </div>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang='ts'>
import Store from './Store.vue'
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

@Component
export default class UpdateStore extends Vue {
    selectedStore: null
    @Getter('storeList') storeList: Array<IStore>
    cloneSelectedStore() {
        return JSON.parse(JSON.stringify(this.selectedStore))
    }
    components: {
        Store: Store
    }
}
</script>
