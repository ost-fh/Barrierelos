import {WebsiteMessage} from "../lib/api-client";

export interface Category {
  category: string;
  translation: string;
}

export const categories: Category[] = [
  { category: WebsiteMessage.category.PRIVATE_AFFILIATED, translation: "Category.private.affiliated" },
  { category: WebsiteMessage.category.PRIVATE_UNIVERSITY, translation: "Category.private.university" },
  { category: WebsiteMessage.category.PRIVATE_NEWS, translation: "Category.private.news" },
  { category: WebsiteMessage.category.PRIVATE_SHOP, translation: "Category.private.shop" },
  { category: WebsiteMessage.category.PRIVATE_OTHER, translation: "Category.private.other" }
]
