<template>
    <div class="ibox-content">
        <ul class="nav nav-tabs">
            <router-link tag="li" :to="{name:'QueryCustomer'}" active-class="active">
                <a>
                    <i class="fa fa-th-large"></i>
                    <span class="nav-label">查詢客戶</span>
                </a>
            </router-link>
            <router-link tag="li" :to="{name:'AddCustomer'}" active-class="active">
                <a>
                    <i class="fa fa-th-large"></i>
                    <span class="nav-label">新增客戶</span>
                </a>
            </router-link>
        </ul>
        <router-view></router-view>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang="ts">
import Vue from 'vue'
export default class CustomerManagement extends Vue {
}
</script>
