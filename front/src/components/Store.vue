<template>
    <div>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">店名</label>
                <div class="col-lg-5">
                    <input type="text" placeholder="店名" class="form-control"
                           required v-model="store.name">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">地址</label>
                <div class="col-lg-5">
                    <input type="email" class="form-control"
                           required v-model="store.addr">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">行動電話</label>
                <div class="col-lg-5">
                    <input name='phone' type="tel" placeholder="電話號碼" class="form-control"
                           required v-model="store.phone">
                </div>
            </div>
            <div class="form-group" v-if='isNew'>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="newStore">新增</button>
                </div>
            </div>
            <div class="form-group" v-else>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="updateStore">更新</button>
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
<script>
    import axios from 'axios'
    import {mapActions, mapGetters} from 'vuex'

    export default {
        props: {
            store: {
                type: Object,
                required: true
            },
            isNew: {
                type: Boolean,
                required: true
            }
        },
        mounted: function () {
        },
        data() {
            return {
                storeID: this.store._id,
                groupInfoList: []
            }
        },
        computed: {

        },
        methods: {
            newStore() {
                if (!this.store.name || this.store.name.length == 0) {
                    alert('店名不能是空的')
                    return
                }

                axios.post('/Store', this.store).then((resp) => {
                    const ret = resp.data
                    if (ret.ok)
                        alert("成功")
                    else
                        alert("失敗:" + ret.msg)
                }).catch((err) => {
                    alert(err)
                })
            },
            updateStore() {
                const url = `/Store/${this.storeID}`
                axios.put(url, this.store).then((resp) => {
                    const ret = resp.data
                    if (ret.ok) {
                        alert("成功")
                    }
                    else
                        alert("失敗:" + ret.msg)
                }).catch((err) => {
                    alert(err)
                })
            }
        },
        components: {}
    }
</script>
