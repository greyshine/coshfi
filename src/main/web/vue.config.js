module.exports = {

  devServer: {

    proxy: {
      '/info': {
        target: 'http://localhost:8080/',
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080/',
        changeOrigin: true,
      },

    }

  },

  /*runtimeCompiler: true,*/
  css: {
    loaderOptions: {
      scss: {
        additionalData: `
            @import "@/assets/global.scss";
            `
      }
    }
  },
}
