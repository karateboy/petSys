<template>
    <div>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-2 control-label">姓名:</label>
                <div class="col-lg-5">
                    <input type="text" class="form-control"
                           required v-model="customer.name">
                </div>
            </div>
            <div class="form-group"><label class="col-lg-1 control-label">生日:</label>
                <div class='col-lg-4'>
                    <datepicker v-model="bdate" language="zh"
                                format="yyyy-MM-dd"></datepicker>
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">地址:</label>
                <div class="col-lg-5">
                    <input type="text" class="form-control"
                           required v-model="customer.addr">
                </div>
            </div>

            <div class="form-group">
                <label class="col-lg-2 control-label">e-mail</label>
                <div class="col-lg-5">
                    <input type="email" class="form-control"
                           required v-model="customer.email">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">行動電話</label>
                <div class="col-lg-5">
                    <input name='phone' type="tel" placeholder="電話號碼" class="form-control"
                           required v-model="customer.phone">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-2 control-label">群組:</label>
                <div class="col-lg-7">
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim"
                               v-for="group in groupInfoList"
                               @click="user.groupID=group.id"
                               :class="{active: user.groupID==group.id }">
                            <input type="radio">{{ group.name }} </label>
                    </div>
                </div>
            </div>

            <div class="form-group" v-if='isNew'>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="newCustomer">新增</button>
                </div>
            </div>
            <div class="form-group" v-else>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" @click.prevent="updateCustomer">更新</button>
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
    import moment from 'moment'
    import Datepicker from 'vuejs-datepicker'

    import {mapActions, mapGetters} from 'vuex'

    export default {
        props: {
            customer: {
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
            }
        },
        computed: {
            bdate: {
                get: function () {
                    if (this.customer.bdate)
                        return new Date(this.customer.bdate)
                    else {
                        const bdate = moment("1990-1-1").toDate()
                        this.customer.bdate = bdate.getTime()
                        return bdate
                    }
                },
                // setter
                set: function (newValue) {
                    this.customer.bdate = newValue.getTime()
                }
            }
        },
        methods: {
            newCustomer() {

                axios.post('/Customer', this.customer).then((resp) => {
                    const ret = resp.data
                    if (ret.ok)
                        alert("成功")
                    else
                        alert("失敗:" + ret.msg)
                }).catch((err) => {
                    alert(err)
                })
            },
            updateCustomer() {
                const url = `/Customer/${encodeURIComponent(this.customer._id)}`
                axios.put(url, this.user).then((resp) => {
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
