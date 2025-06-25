
    window.onload = () => {
      const ui = SwaggerUIBundle({
        url: "http://localhost:8080/api/v3/api-docs",
        dom_id: '#swagger-ui',
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        layout: "StandaloneLayout",
    //    withCredentials: true,

        requestInterceptor: (req) => {
          req.credentials = 'include'; // tells browser to include cookies
          return req;
        }
      });
    };
