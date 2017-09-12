<template>
    <div>
        <br>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-1 control-label">姓名:</label>
                <div class="col-lg-4"><input type="text" placeholder="姓名" autofocus class="form-control" v-model="queryParam.name">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">行動電話:</label>
                <div class="col-lg-4"><input type="text" class="form-control" placeholder="電話" v-model="queryParam.phone"></div>
            </div>
            <div v-if="!selectCustomer">
                <div class="form-group">
                    <label class="col-lg-1 control-label">上次來店時間從:</label>
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
            </div>
            <div class="form-group">
                <div class="col-lg-offset-1">
                    <button class='btn btn-primary' @click='query'>查詢</button>
                </div>
            </div>
        </div>
        <div v-if='display'>
            <customer-list :url="queryUrl" :param="queryParam" :selectCustomer="true"></customer-list>
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
import CustomerList from './CustomerList.vue'

@Component({
    components: {
        CustomerList,
        Datepicker
    }
})
export default class QueryCustomer extends Vue {
    @Prop({
        type: Boolean,
        default: false
    }) selectCustomer: boolean

    display = false
    queryUrl = '/QueryCustomer'
    queryParam: {
        start?: number
        end?: number
        name?: string
        phone?: string
    } = {}

    get start(): Date {
        if (this.queryParam.start)
            return moment(this.queryParam.start).toDate()
        else
            return new Date(0)
    }

    set start(newValue: Date) {
        this.queryParam.start = newValue.getTime()
    }

    get end() {
        if (this.queryParam.end)
            return moment(this.queryParam.end).toDate()
        else
            return new Date(0)
    }

    set end(newValue: Date) {
        this.queryParam.end = newValue.getTime()
    }



    prepareParam() {
        if (this.queryParam.name == "")
            this.queryParam.name = undefined

        if (this.queryParam.phone == '')
            this.queryParam.phone = undefined

    }

    query() {
        this.prepareParam()
        if (!this.display)
            this.display = true

        this.queryParam = Object.assign({}, this.queryParam)
    }
}
</script>
