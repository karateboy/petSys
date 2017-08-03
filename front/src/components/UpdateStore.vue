<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">店鋪:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim"
                               v-for="store in storeList"
                               @click="selectedStore=store">
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
<script>
    import Store from './Store.vue'
    import axios from 'axios'
    import {mapGetters, mapActions} from 'vuex'

    export default {
        data() {
            return {
                selectedStore: null
            }
        },
        mounted: function () {

        },
        computed: {
            ...mapGetters(['storeList'])
        },
        methods: {
            cloneSelectedStore(){
                return JSON.parse(JSON.stringify(this.selectedStore))
            }
         },
        components: {
            Store
        }
    }
</script>
