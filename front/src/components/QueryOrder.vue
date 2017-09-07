<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-1 control-label">訂單號碼:</label>
                <div class="col-lg-4"><input type="text" placeholder="訂單號碼" autofocus class="form-control" v-model="queryParam._id">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">出貨日從:</label>
                <div class="col-lg-5">
                    <div class="input-daterange input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </span>
                        <datepicker v-model="start" language="zh" format="yyyy-MM-dd"></datepicker>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">至(不含):</label>
                <div class="col-lg-5">
                    <div class="input-daterange input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </span>
                        <datepicker v-model="end" language="zh" format="yyyy-MM-dd"></datepicker>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-offset-1">
                    <button class='btn btn-primary' @click='query'>查詢</button>
                </div>
            </div>
        </div>
        <div v-if='display'>
            <order-list :url="queryUrl" :param="queryParam"></order-list>
        </div>
    </div>
</template>
<style scoped>

</style>
<script lang="ts">
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'

import axios from 'axios'
import moment from 'moment'
import Datepicker from 'vuejs-datepicker'
import OrderList from "./OrderList.vue"

export default class QueryOrder extends Vue {
    display = false
    queryUrl = '/QueryOrder'
    queryParam = {
        _id: null,
        start: 0,
        end: 0
    }
    get start(): Date {
        if (this.queryParam.start)
            return moment(this.queryParam.start).toDate()
        else
            return new Date()
    }

    set start(newValue: Date) {
        this.queryParam.start = newValue.getTime()
    }

    get end(): Date {
        if (this.queryParam.end)
            return moment(this.queryParam.end).toDate()
        else
            return new Date()
    }

    set end(newValue: Date) {
        this.queryParam.end = newValue.getTime()
    }

    prepareParam() {
        if (this.queryParam._id == "")
            this.queryParam._id = null
    }

    query() {
        this.prepareParam()
        if (!this.display)
            this.display = true

        this.queryParam = Object.assign({}, this.queryParam)
    }


    components: {
        OrderList: OrderList,
        Datepicker: any
    }
}
</script>
