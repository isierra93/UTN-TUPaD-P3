import type { CartItem } from "../../types/CartItem";
import { getCart } from "../../utils/getCart";
import { protectRoute } from "../../main";

protectRoute();

const carrito: CartItem[] = await getCart();

console.log(carrito);
