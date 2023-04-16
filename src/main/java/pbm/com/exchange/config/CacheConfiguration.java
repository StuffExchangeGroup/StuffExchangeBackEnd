package pbm.com.exchange.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, pbm.com.exchange.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, pbm.com.exchange.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, pbm.com.exchange.domain.User.class.getName());
            createCache(cm, pbm.com.exchange.domain.Authority.class.getName());
            createCache(cm, pbm.com.exchange.domain.User.class.getName() + ".authorities");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName());
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".favorites");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".ownerExchanges");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".exchangerExchanges");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".purchases");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".products");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".notificationTokens");
            createCache(cm, pbm.com.exchange.domain.Nationality.class.getName());
            createCache(cm, pbm.com.exchange.domain.Nationality.class.getName() + ".provinces");
            createCache(cm, pbm.com.exchange.domain.Category.class.getName());
            createCache(cm, pbm.com.exchange.domain.Category.class.getName() + ".productCategories");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName());
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".images");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".sendExchanges");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".receiveExchanges");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".productCategories");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".favorites");
            createCache(cm, pbm.com.exchange.domain.ProductCategory.class.getName());
            createCache(cm, pbm.com.exchange.domain.Image.class.getName());
            createCache(cm, pbm.com.exchange.domain.Favorite.class.getName());
            createCache(cm, pbm.com.exchange.domain.Province.class.getName());
            createCache(cm, pbm.com.exchange.domain.Province.class.getName() + ".cities");
            createCache(cm, pbm.com.exchange.domain.Exchange.class.getName());
            createCache(cm, pbm.com.exchange.domain.Purchase.class.getName());
            createCache(cm, pbm.com.exchange.domain.Message.class.getName());
            createCache(cm, pbm.com.exchange.domain.File.class.getName());
            createCache(cm, pbm.com.exchange.domain.Level.class.getName());
            createCache(cm, pbm.com.exchange.domain.Level.class.getName() + ".profiles");
            createCache(cm, pbm.com.exchange.domain.City.class.getName());
            createCache(cm, pbm.com.exchange.domain.City.class.getName() + ".profiles");
            createCache(cm, pbm.com.exchange.domain.City.class.getName() + ".products");
            createCache(cm, pbm.com.exchange.domain.NotificationToken.class.getName());
            createCache(cm, pbm.com.exchange.domain.AppConfiguration.class.getName());
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".auctions");
            createCache(cm, pbm.com.exchange.domain.Profile.class.getName() + ".comments");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".auctions");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".comments");
            createCache(cm, pbm.com.exchange.domain.Product.class.getName() + ".purposes");
            createCache(cm, pbm.com.exchange.domain.Auction.class.getName());
            createCache(cm, pbm.com.exchange.domain.Comment.class.getName());
            createCache(cm, pbm.com.exchange.domain.Rating.class.getName());
            createCache(cm, pbm.com.exchange.domain.ProductPurpose.class.getName());
            createCache(cm, pbm.com.exchange.domain.ProductPurpose.class.getName() + ".products");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
