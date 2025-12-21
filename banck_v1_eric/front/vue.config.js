module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://47.108.59.70:8090',
        changeOrigin: true,
        secure: false
      },
      '/uploads': {
        target: 'http://47.108.59.70:8090',
        changeOrigin: true,
        secure: false,
        ws: false, // 禁用 WebSocket 代理
        pathRewrite: {
          '^/uploads': '/uploads' // 保持路径不变
        }
      }
    }
  }
}

