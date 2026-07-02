package org.example;

import org.example.model.*;
import org.example.model.enums.*;
import org.example.repository.*;
import org.example.util.JPAUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final CategoriaRepository catRepo     = new CategoriaRepository();
    private static final ProductoRepository  prodRepo    = new ProductoRepository();
    private static final UsuarioRepository   usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository    pedidoRepo  = new PedidoRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n==============================");
            System.out.println("       MENU PRINCIPAL         ");
            System.out.println("==============================");
            System.out.println("1. Gestionar Categorias");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Gestionar Usuarios");
            System.out.println("4. Gestionar Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuUsuarios();
                case 4 -> menuPedidos();
                case 5 -> menuReportes();
                case 0 -> salir = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
        JPAUtil.close();
        System.out.println("Hasta luego.");
    }

    // ─────────────────────────────────────────────────────────────────
    // CATEGORIAS
    // ─────────────────────────────────────────────────────────────────

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Categorias ---");
            System.out.println("1. Alta");
            System.out.println("2. Baja");
            System.out.println("3. Modificacion");
            System.out.println("4. Listado");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> altaCategoria();
                case 2 -> bajaCategoria();
                case 3 -> modificarCategoria();
                case 4 -> listarCategorias();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaCategoria() {
        System.out.println("\n-- Alta de Categoria --");
        String nombre = leerTextoObligatorio("Nombre: ");
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();

        Categoria cat = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .build();

        Categoria guardada = catRepo.guardar(cat);
        System.out.println("Categoria creada con ID: " + guardada.getId());
    }

    private static void bajaCategoria() {
        System.out.println("\n-- Baja de Categoria --");
        List<Categoria> activas = listarCategorias();
        if (activas.isEmpty()) return;

        Long id = leerLong("ID de la categoria a dar de baja: ");
        Optional<Categoria> opt = catRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ninguna categoria activa con ID " + id + ".");
            return;
        }

        catRepo.eliminarLogico(id);
        System.out.println("Categoria '" + opt.get().getNombre() + "' dada de baja correctamente.");
    }

    private static void modificarCategoria() {
        System.out.println("\n-- Modificacion de Categoria --");
        List<Categoria> activas = listarCategorias();
        if (activas.isEmpty()) return;

        Long id = leerLong("ID de la categoria a modificar: ");
        Optional<Categoria> opt = catRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ninguna categoria activa con ID " + id + ".");
            return;
        }

        Categoria cat = opt.get();

        System.out.println("Nombre actual: " + cat.getNombre());
        System.out.print("Nuevo nombre (Enter para mantener): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) cat.setNombre(nuevoNombre);

        System.out.println("Descripcion actual: " + cat.getDescripcion());
        System.out.print("Nueva descripcion (Enter para mantener): ");
        String nuevaDesc = scanner.nextLine().trim();
        if (!nuevaDesc.isEmpty()) cat.setDescripcion(nuevaDesc);

        catRepo.guardar(cat);
        System.out.println("Categoria actualizada correctamente.");
    }

    private static List<Categoria> listarCategorias() {
        List<Categoria> activas = catRepo.listarActivos();
        if (activas.isEmpty()) {
            System.out.println("No hay categorias activas.");
        } else {
            System.out.println("\n ID   | Nombre               | Descripcion");
            System.out.println("------|----------------------|-----------------------------");
            for (Categoria c : activas) {
                System.out.printf(" %-5d| %-21s| %s%n", c.getId(), c.getNombre(), c.getDescripcion());
            }
        }
        return activas;
    }

    // ─────────────────────────────────────────────────────────────────
    // PRODUCTOS
    // ─────────────────────────────────────────────────────────────────

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Productos ---");
            System.out.println("1. Alta");
            System.out.println("2. Baja");
            System.out.println("3. Modificacion");
            System.out.println("4. Listado");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> altaProducto();
                case 2 -> bajaProducto();
                case 3 -> modificarProducto();
                case 4 -> listarProductos();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaProducto() {
        System.out.println("\n-- Alta de Producto --");

        List<Categoria> cats = catRepo.listarActivos();
        if (cats.isEmpty()) {
            System.out.println("No hay categorias activas. Cree una categoria primero.");
            return;
        }

        listarCategorias();
        Long catId = leerLong("Seleccione ID de categoria: ");
        if (cats.stream().noneMatch(c -> c.getId().equals(catId))) {
            System.out.println("Error: ID de categoria invalido.");
            return;
        }

        String nombre = leerTextoObligatorio("Nombre del producto: ");
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();
        double precio = leerDoublePositivo("Precio (mayor a 0): ");
        int stock = leerIntNoNegativo("Stock (mayor o igual a 0): ");
        System.out.print("Imagen (URL, opcional): ");
        String imagen = scanner.nextLine().trim();

        Producto prod = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stock)
                .imagen(imagen)
                .disponible(true)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .build();

        catRepo.agregarProducto(catId, prod);
        System.out.println("Producto '" + nombre + "' creado correctamente.");
    }

    private static void bajaProducto() {
        System.out.println("\n-- Baja de Producto --");
        List<Producto> activos = listarProductos();
        if (activos.isEmpty()) return;

        Long id = leerLong("ID del producto a dar de baja: ");
        Optional<Producto> opt = prodRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun producto activo con ID " + id + ".");
            return;
        }

        prodRepo.eliminarLogico(id);
        System.out.println("Producto '" + opt.get().getNombre() + "' dado de baja correctamente.");
    }

    private static void modificarProducto() {
        System.out.println("\n-- Modificacion de Producto --");
        List<Producto> activos = listarProductos();
        if (activos.isEmpty()) return;

        Long id = leerLong("ID del producto a modificar: ");
        Optional<Producto> opt = prodRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun producto activo con ID " + id + ".");
            return;
        }

        Producto prod = opt.get();

        System.out.println("Nombre actual: " + prod.getNombre());
        System.out.print("Nuevo nombre (Enter para mantener): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) prod.setNombre(nuevoNombre);

        System.out.printf("Precio actual: $%.2f%n", prod.getPrecio());
        System.out.print("Nuevo precio (Enter para mantener): ");
        String precioStr = scanner.nextLine().trim();
        if (!precioStr.isEmpty()) {
            try {
                double p = Double.parseDouble(precioStr);
                if (p > 0) prod.setPrecio(p);
                else System.out.println("Debe ser mayor a 0. Se mantiene el valor anterior.");
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Se mantiene el precio anterior.");
            }
        }

        System.out.println("Stock actual: " + prod.getStock());
        System.out.print("Nuevo stock (Enter para mantener): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int s = Integer.parseInt(stockStr);
                if (s >= 0) prod.setStock(s);
                else System.out.println("No puede ser negativo. Se mantiene el valor anterior.");
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Se mantiene el stock anterior.");
            }
        }

        prodRepo.guardar(prod);
        System.out.println("Producto actualizado correctamente.");
    }

    private static List<Producto> listarProductos() {
        List<Producto> activos = prodRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay productos activos.");
        } else {
            System.out.println("\n ID   | Nombre               | Precio     | Stock | Disp.");
            System.out.println("------|----------------------|------------|-------|------");
            for (Producto p : activos) {
                System.out.printf(" %-5d| %-21s| $%-10.2f| %-6d| %s%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock(),
                        Boolean.TRUE.equals(p.getDisponible()) ? "Si" : "No");
            }
        }
        return activos;
    }

    // ─────────────────────────────────────────────────────────────────
    // USUARIOS
    // ─────────────────────────────────────────────────────────────────

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Usuarios ---");
            System.out.println("1. Alta");
            System.out.println("2. Baja");
            System.out.println("3. Modificacion");
            System.out.println("4. Listado");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> altaUsuario();
                case 2 -> bajaUsuario();
                case 3 -> modificarUsuario();
                case 4 -> listarUsuarios();
                case 5 -> buscarPorMail();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaUsuario() {
        System.out.println("\n-- Alta de Usuario --");
        String nombre   = leerTextoObligatorio("Nombre: ");
        String apellido = leerTextoObligatorio("Apellido: ");
        String mail     = leerTextoObligatorio("Mail: ");

        if (usuarioRepo.buscarPorMail(mail).isPresent()) {
            System.out.println("Error: ya existe un usuario con ese mail.");
            return;
        }

        System.out.print("Celular: ");
        String celular = scanner.nextLine().trim();
        String clave   = leerTextoObligatorio("Clave: ");

        System.out.println("Rol: 1. ADMIN  2. USUARIO");
        Rol rol = leerInt("Opcion: ") == 1 ? Rol.ADMIN : Rol.USUARIO;

        Usuario u = Usuario.builder()
                .nombre(nombre).apellido(apellido).mail(mail)
                .celular(celular).clavePersonal(clave).rol(rol)
                .eliminado(false).createdAt(LocalDateTime.now())
                .build();

        Usuario guardado = usuarioRepo.guardar(u);
        System.out.println("Usuario creado con ID: " + guardado.getId());
    }

    private static void bajaUsuario() {
        System.out.println("\n-- Baja de Usuario --");
        List<Usuario> activos = listarUsuarios();
        if (activos.isEmpty()) return;

        Long id = leerLong("ID del usuario a dar de baja: ");
        Optional<Usuario> opt = usuarioRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun usuario activo con ID " + id + ".");
            return;
        }

        usuarioRepo.eliminarLogico(id);
        System.out.println("Usuario '" + opt.get().getNombre() + " " + opt.get().getApellido()
                + "' dado de baja. Sus pedidos permanecen en el sistema.");
    }

    private static void modificarUsuario() {
        System.out.println("\n-- Modificacion de Usuario --");
        List<Usuario> activos = listarUsuarios();
        if (activos.isEmpty()) return;

        Long id = leerLong("ID del usuario a modificar: ");
        Optional<Usuario> opt = usuarioRepo.buscarPorId(id);

        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun usuario activo con ID " + id + ".");
            return;
        }

        Usuario u = opt.get();

        System.out.println("Nombre actual: " + u.getNombre());
        System.out.print("Nuevo nombre (Enter para mantener): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) u.setNombre(nombre);

        System.out.println("Apellido actual: " + u.getApellido());
        System.out.print("Nuevo apellido (Enter para mantener): ");
        String apellido = scanner.nextLine().trim();
        if (!apellido.isEmpty()) u.setApellido(apellido);

        System.out.println("Celular actual: " + u.getCelular());
        System.out.print("Nuevo celular (Enter para mantener): ");
        String celular = scanner.nextLine().trim();
        if (!celular.isEmpty()) u.setCelular(celular);

        usuarioRepo.guardar(u);
        System.out.println("Usuario actualizado correctamente.");
    }

    private static List<Usuario> listarUsuarios() {
        List<Usuario> activos = usuarioRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay usuarios activos.");
        } else {
            System.out.println("\n ID   | Nombre               | Mail                          | Rol");
            System.out.println("------|----------------------|-------------------------------|--------");
            for (Usuario u : activos) {
                System.out.printf(" %-5d| %-21s| %-30s| %s%n",
                        u.getId(), u.getNombre() + " " + u.getApellido(), u.getMail(), u.getRol());
            }
        }
        return activos;
    }

    private static void buscarPorMail() {
        System.out.println("\n-- Buscar Usuario por Mail --");
        String mail = leerTextoObligatorio("Mail: ");
        usuarioRepo.buscarPorMail(mail).ifPresentOrElse(
                u -> System.out.printf("ID: %d | %s %s | %s | Cel: %s | Rol: %s%n",
                        u.getId(), u.getNombre(), u.getApellido(), u.getMail(), u.getCelular(), u.getRol()),
                () -> System.out.println("No se encontro ningun usuario activo con ese mail.")
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // PEDIDOS
    // ─────────────────────────────────────────────────────────────────

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Pedidos ---");
            System.out.println("1. Alta de pedido");
            System.out.println("2. Cambiar estado");
            System.out.println("3. Baja logica");
            System.out.println("4. Listado");
            System.out.println("5. Pedidos por usuario");
            System.out.println("6. Pedidos por estado");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> altaPedido();
                case 2 -> cambiarEstadoPedido();
                case 3 -> bajaPedido();
                case 4 -> listarTodosPedidos();
                case 5 -> listarPedidosPorUsuario();
                case 6 -> listarPedidosPorEstado();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaPedido() {
        System.out.println("\n-- Alta de Pedido --");

        List<Usuario> usuarios = listarUsuarios();
        if (usuarios.isEmpty()) return;

        long usuarioId = leerLong("ID del usuario: ");
        if (usuarios.stream().noneMatch(u -> u.getId().equals(usuarioId))) {
            System.out.println("Error: usuario no encontrado.");
            return;
        }

        List<Producto> productos = listarProductos();
        if (productos.isEmpty()) return;

        System.out.println("\nForma de pago:");
        FormaPago[] formas = FormaPago.values();
        for (int i = 0; i < formas.length; i++) {
            System.out.println((i + 1) + ". " + formas[i]);
        }
        int fpOp = leerInt("Opcion: ");
        if (fpOp < 1 || fpOp > formas.length) {
            System.out.println("Opcion invalida.");
            return;
        }
        FormaPago formaPago = formas[fpOp - 1];

        // Lista temporal: solo ID de producto y cantidad. Nada se persiste hasta confirmar.
        Map<Long, Integer> items = new LinkedHashMap<>();
        while (true) {
            long prodId = leerLong("ID del producto a agregar (0 para terminar): ");
            if (prodId == 0) {
                if (items.isEmpty()) {
                    System.out.println("Debe agregar al menos un producto.");
                    continue;
                }
                break;
            }

            Optional<Producto> prodOpt = productos.stream()
                    .filter(p -> p.getId().equals(prodId))
                    .findFirst();
            if (prodOpt.isEmpty()) {
                System.out.println("ID invalido.");
                continue;
            }
            Producto prod = prodOpt.get();
            if (items.containsKey(prodId)) {
                System.out.println("Ese producto ya fue agregado al pedido.");
                continue;
            }
            if (!Boolean.TRUE.equals(prod.getDisponible())) {
                System.out.println("El producto no esta disponible.");
                continue;
            }
            int cantidad = leerIntNoNegativo("Cantidad: ");
            if (cantidad == 0) continue;
            if (prod.getStock() < cantidad) {
                System.out.printf("Stock insuficiente. Disponible: %d%n", prod.getStock());
                continue;
            }
            items.put(prodId, cantidad);
            System.out.printf("Agregado: %s x%d%n", prod.getNombre(), cantidad);
        }

        try {
            Pedido creado = usuarioRepo.crearPedido(usuarioId, formaPago, items);
            System.out.println("\nPedido creado con exito:");
            System.out.println("ID: " + creado.getId() + " | Fecha: " + creado.getFecha()
                    + " | Forma de pago: " + creado.getFormaPago());
            for (DetallePedido d : creado.getDetalles()) {
                System.out.printf("  - %s x%d = $%.2f%n",
                        d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal());
            }
            System.out.printf("Total: $%.2f%n", creado.getTotal());
        } catch (Exception e) {
            System.out.println("Error al crear el pedido: " + e.getMessage());
        }
    }

    private static void cambiarEstadoPedido() {
        System.out.println("\n-- Cambiar Estado de Pedido --");

        List<Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos activos.");
            return;
        }
        imprimirPedidos(pedidos);

        long id = leerLong("ID del pedido: ");
        Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun pedido activo con ID " + id + ".");
            return;
        }
        System.out.println("Estado actual: " + opt.get().getEstado());

        System.out.println("Nuevo estado:");
        Estado[] estados = Estado.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        int estOp = leerInt("Opcion: ");
        if (estOp < 1 || estOp > estados.length) {
            System.out.println("Opcion invalida.");
            return;
        }

        Estado nuevoEstado = estados[estOp - 1];
        boolean ok = pedidoRepo.cambiarEstado(id, nuevoEstado);
        if (ok) {
            System.out.println("Pedido ID " + id + " actualizado a estado: " + nuevoEstado);
        } else {
            System.out.println("Error: pedido no encontrado.");
        }
    }

    private static void bajaPedido() {
        System.out.println("\n-- Baja de Pedido --");
        List<Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos activos.");
            return;
        }
        imprimirPedidos(pedidos);

        long id = leerLong("ID del pedido a dar de baja: ");
        Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe ningun pedido activo con ID " + id + ".");
            return;
        }

        double total = opt.get().getTotal() == null ? 0.0 : opt.get().getTotal();
        pedidoRepo.eliminarLogico(id);
        System.out.printf("Pedido ID %d dado de baja. Total: $%.2f (el stock NO se restaura).%n", id, total);
    }

    private static void listarTodosPedidos() {
        System.out.println("\n-- Listado de Pedidos --");
        List<Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos activos.");
        } else {
            imprimirPedidos(pedidos);
        }
    }

    private static void listarPedidosPorEstado() {
        System.out.println("\n-- Pedidos por Estado --");
        Estado[] estados = Estado.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        int op = leerInt("Opcion: ");
        if (op < 1 || op > estados.length) {
            System.out.println("Opcion invalida.");
            return;
        }
        Estado estado = estados[op - 1];
        List<Pedido> pedidos = pedidoRepo.buscarPorEstado(estado);
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos con estado " + estado + ".");
        } else {
            imprimirPedidos(pedidos);
        }
    }

    private static void listarPedidosPorUsuario() {
        System.out.println("\n-- Pedidos por Usuario --");
        List<Usuario> usuarios = listarUsuarios();
        if (usuarios.isEmpty()) return;

        long id = leerLong("ID del usuario: ");
        List<Pedido> pedidos = usuarioRepo.buscarPedidosPorUsuario(id);
        if (pedidos.isEmpty()) {
            System.out.println("El usuario no tiene pedidos activos.");
        } else {
            imprimirPedidos(pedidos);
        }
    }

    // Como la relacion Usuario->Pedido es unidireccional, Pedido no conoce a su Usuario.
    // Para mostrar el nombre del cliente en los listados se arma un mapa {idPedido -> Usuario}
    // recorriendo los usuarios activos y sus pedidos.
    private static Map<Long, Usuario> mapaPedidoUsuario() {
        Map<Long, Usuario> map = new HashMap<>();
        for (Usuario u : usuarioRepo.listarActivos()) {
            for (Pedido p : usuarioRepo.buscarPedidosPorUsuario(u.getId())) {
                map.put(p.getId(), u);
            }
        }
        return map;
    }

    private static void imprimirPedidos(List<Pedido> pedidos) {
        Map<Long, Usuario> usuarios = mapaPedidoUsuario();
        System.out.println("\n ID   | Fecha      | Estado      | Forma Pago    | Usuario              | Total");
        System.out.println("------|------------|-------------|---------------|----------------------|----------");
        for (Pedido p : pedidos) {
            Usuario u = usuarios.get(p.getId());
            String nombre = u == null ? "(desconocido)" : u.getNombre() + " " + u.getApellido();
            System.out.printf(" %-5d| %-11s| %-12s| %-14s| %-21s| $%.2f%n",
                    p.getId(), p.getFecha(), p.getEstado(), p.getFormaPago(), nombre, p.getTotal());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // REPORTES
    // ─────────────────────────────────────────────────────────────────

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Reportes ---");
            System.out.println("1. Productos por categoria");
            System.out.println("2. Pedidos por usuario");
            System.out.println("3. Pedidos por estado");
            System.out.println("4. Total facturado");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> reporteProductosPorCategoria();
                case 2 -> listarPedidosPorUsuario();
                case 3 -> listarPedidosPorEstado();
                case 4 -> reporteTotalFacturado();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void reporteProductosPorCategoria() {
        System.out.println("\n-- Productos por Categoria --");
        List<Categoria> cats = listarCategorias();
        if (cats.isEmpty()) return;

        long id = leerLong("Seleccione ID de categoria: ");
        if (cats.stream().noneMatch(c -> c.getId().equals(id))) {
            System.out.println("Error: ID de categoria invalido.");
            return;
        }

        List<Producto> productos = catRepo.buscarProductosPorCategoria(id);
        if (productos.isEmpty()) {
            System.out.println("La categoria no tiene productos activos.");
        } else {
            System.out.println("\n ID   | Nombre               | Precio     | Stock");
            System.out.println("------|----------------------|------------|------");
            for (Producto p : productos) {
                System.out.printf(" %-5d| %-21s| $%-10.2f| %d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
    }

    private static void reporteTotalFacturado() {
        System.out.println("\n-- Total Facturado (pedidos TERMINADO) --");
        List<Pedido> terminados = pedidoRepo.buscarPorEstado(Estado.TERMINADO);
        double total = terminados.stream()
                .mapToDouble(p -> p.getTotal() == null ? 0.0 : p.getTotal())
                .sum();
        System.out.println(String.format(Locale.US, "Total facturado: $%.2f", total));
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS DE ENTRADA
    // ─────────────────────────────────────────────────────────────────

    private static int leerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    private static long leerLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private static String leerTextoObligatorio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("Este campo es obligatorio.");
        }
    }

    private static double leerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val > 0) return val;
                System.out.println("Debe ser mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un valor numerico valido.");
            }
        }
    }

    private static int leerIntNoNegativo(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val >= 0) return val;
                System.out.println("No puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }
}
