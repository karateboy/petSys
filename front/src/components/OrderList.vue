<template>
    <div>
        <div v-if="orderList.length != 0">
            <table class="table table-hover table-bordered table-condensed">
                <thead>
                    <tr class='info'>
                        <th></th>
                        <th>訂單編號</th>
                        <th>客戶編號</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(order, index) in orderList" :class='{success: selectedIndex == index}'>
                        <td>
                        </td>
                        <td>{{ order._id}}</td>
                        <td>{{ order.customerId}}</td>
                    </tr>
                </tbody>
            </table>
            <pagination for="orderList" :records="total" :per-page="5" count-text="第{from}到第{to}筆/共{count}筆|{count} 筆|1筆"></pagination>
        </div>
        <div v-else class="alert alert-info" role="alert">沒有符合的訂單</div>
        <hr>
        <div v-if="display=='detail'">
            <order-detail></order-detail>
        </div>
    </div>
</template>
<style scoped>
body {}
</style>
<script lang="ts">
import { mapActions } from 'vuex'
import OrderDetail from './OrderDetail.vue'
import moment from 'moment'
import { Pagination, PaginationEvent } from 'vue-pagination-2'
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
import IOrder from './IOrder'
@Component
export default class OrderList extends Vue {
    @Prop({
        type: String,
        required: true
    }) url: string
    @Prop({
        type: Object
    }) param: object

    orderList: Array<IOrder> = []
    skip = 0
    limit = 5
    total = 0
    display = false
    selectedIndex = -1
    order: IOrder
    mounted() {
        this.fetchOrder(this.skip, this.limit)
        PaginationEvent.$on('vue-pagination::orderList', this.handlePageChange)
    }

    @Watch("url") urlWatch(newUrl: string) {
        console.log(newUrl)
        this.fetchOrder(this.skip, this.limit)
    }
    @Watch("param") paramWatch(newParam: object) {
        console.log(newParam)
        this.fetchOrder(this.skip, this.limit)
    }

    processResp(resp: { data: Array<IOrder> }) {
        this.orderList.splice(0, this.orderList.length)
        for (let v of resp.data) {
            this.orderList.push(v)
        }
    }

    fetchOrder(skip: number, limit: number) {
        let request_url = `${this.url}/${skip}/${limit}`

        if (this.param) {
            axios.post(request_url, this.param).then(this.processResp).catch((err) => {
                alert(err)
            })
        } else {
            axios.get(request_url).then(this.processResp).catch((err) => {
                alert(err)
            })
        }
        this.fetchOrderCount()
    }

    fetchOrderCount() {
        let request_url = `${this.url}/count`
        if (this.param) {
            axios.post(request_url, this.param).then(resp => {
                this.total = resp.data
            }).catch((err) => {
                alert(err)
            })
        } else {
            axios.get(request_url).then(resp => {
                this.total = resp.data
            }).catch((err) => {
                alert(err)
            })
        }
    }

    handlePageChange(page: number) {
        this.skip = (page - 1) * this.limit
        this.fetchOrder(this.skip, this.limit)
    }

    displayDate(millis: number) {
        const mm = moment(millis)
        const dateStr = mm.format('YYYY-MM-DD')
        const afterStr = mm.fromNow()
        return dateStr + " (" + afterStr + ")";
    }

    displayOrder(idx: number) {
        this.selectedIndex = idx
        this.display = true;
    }

    closeOrder(idx: number) {
        axios.post("/CloseOrder/" + this.orderList[idx]._id).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("訂單結案")
                this.orderList[idx].active = false
            } else {
                alert(ret.msg)
            }
        }).catch((err) => {
            alert(err)
        })
    }

    reopenOrder(idx: number) {
        axios.post("/ReopenOrder/" + this.orderList[idx]._id).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("訂單重啟")
                this.orderList[idx].active = true
            } else {
                alert(ret.msg)
            }
        }).catch((err) => {
            alert(err)
        })
    }

    deleteOrder(idx: number) {
        axios.delete("/Order/" + this.orderList[idx]._id).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("訂單刪除")
                this.orderList.splice(idx, 1)
            } else {
                alert(ret.msg)
            }
        }).catch((err) => {
            alert(err)
        })
    }
    components: {
        OrderDetail: OrderDetail
        Pagination: Pagination
    }
}
</script>
