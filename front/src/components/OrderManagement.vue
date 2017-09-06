<template>
    <div class="ibox flot-e-margins">
        <div class="ibox-content">
            <ul class="nav nav-tabs">
                <router-link to='/Order/New' tag='li' role="presentation" active-class='active'>
                    <a>新增訂單</a>
                </router-link>
                <router-link to='/Order/Active' tag='li' role="presentation" active-class='active'>
                    <a>未結帳訂單</a>
                </router-link>
                <router-link to='/Order/Query' tag='li' role="presentation" active-class='active'>
                    <a>查詢訂單</a>
                </router-link>
            </ul>
            <router-view></router-view>
        </div>
    </div>
</template>
<style scoped>

</style>
<script lang="ts">
import Vue from 'vue'
export default class OrderManagement extends Vue {
}
</script>
