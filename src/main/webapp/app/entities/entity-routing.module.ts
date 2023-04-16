import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'profile',
        data: { pageTitle: 'stuffExchangeApp.profile.home.title' },
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
      },
      {
        path: 'nationality',
        data: { pageTitle: 'stuffExchangeApp.nationality.home.title' },
        loadChildren: () => import('./nationality/nationality.module').then(m => m.NationalityModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'stuffExchangeApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'stuffExchangeApp.product.home.title' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'product-category',
        data: { pageTitle: 'stuffExchangeApp.productCategory.home.title' },
        loadChildren: () => import('./product-category/product-category.module').then(m => m.ProductCategoryModule),
      },
      {
        path: 'image',
        data: { pageTitle: 'stuffExchangeApp.image.home.title' },
        loadChildren: () => import('./image/image.module').then(m => m.ImageModule),
      },
      {
        path: 'favorite',
        data: { pageTitle: 'stuffExchangeApp.favorite.home.title' },
        loadChildren: () => import('./favorite/favorite.module').then(m => m.FavoriteModule),
      },
      {
        path: 'province',
        data: { pageTitle: 'stuffExchangeApp.province.home.title' },
        loadChildren: () => import('./province/province.module').then(m => m.ProvinceModule),
      },
      {
        path: 'exchange',
        data: { pageTitle: 'stuffExchangeApp.exchange.home.title' },
        loadChildren: () => import('./exchange/exchange.module').then(m => m.ExchangeModule),
      },
      {
        path: 'purchase',
        data: { pageTitle: 'stuffExchangeApp.purchase.home.title' },
        loadChildren: () => import('./purchase/purchase.module').then(m => m.PurchaseModule),
      },
      {
        path: 'message',
        data: { pageTitle: 'stuffExchangeApp.message.home.title' },
        loadChildren: () => import('./message/message.module').then(m => m.MessageModule),
      },
      {
        path: 'file',
        data: { pageTitle: 'stuffExchangeApp.file.home.title' },
        loadChildren: () => import('./file/file.module').then(m => m.FileModule),
      },
      {
        path: 'level',
        data: { pageTitle: 'stuffExchangeApp.level.home.title' },
        loadChildren: () => import('./level/level.module').then(m => m.LevelModule),
      },
      {
        path: 'city',
        data: { pageTitle: 'stuffExchangeApp.city.home.title' },
        loadChildren: () => import('./city/city.module').then(m => m.CityModule),
      },
      {
        path: 'notification-token',
        data: { pageTitle: 'stuffExchangeApp.notificationToken.home.title' },
        loadChildren: () => import('./notification-token/notification-token.module').then(m => m.NotificationTokenModule),
      },
      {
        path: 'app-configuration',
        data: { pageTitle: 'stuffExchangeApp.appConfiguration.home.title' },
        loadChildren: () => import('./app-configuration/app-configuration.module').then(m => m.AppConfigurationModule),
      },
      {
        path: 'auction',
        data: { pageTitle: 'stuffExchangeApp.auction.home.title' },
        loadChildren: () => import('./auction/auction.module').then(m => m.AuctionModule),
      },
      {
        path: 'comment',
        data: { pageTitle: 'stuffExchangeApp.comment.home.title' },
        loadChildren: () => import('./comment/comment.module').then(m => m.CommentModule),
      },
      {
        path: 'rating',
        data: { pageTitle: 'stuffExchangeApp.rating.home.title' },
        loadChildren: () => import('./rating/rating.module').then(m => m.RatingModule),
      },
      {
        path: 'product-purpose',
        data: { pageTitle: 'stuffExchangeApp.productPurpose.home.title' },
        loadChildren: () => import('./product-purpose/product-purpose.module').then(m => m.ProductPurposeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
