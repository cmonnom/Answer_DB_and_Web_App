package utsw.bicf.answer.test.api;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import utsw.bicf.answer.dao.ModelDAO;

@EnableWebMvc
public class WebDispatcherConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public ModelDAO getModelDAO() {
		return new ModelDAO();
	}
	
	@Bean
	public ViewResolver viewResolver() {
	    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	    resolver.setPrefix("/WEB-INF/view/templates/");
	    resolver.setSuffix(".jsp");
	    resolver.setExposeContextBeansAsAttributes(true);
	    return resolver;
	}



	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	    configurer.enable();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
}
