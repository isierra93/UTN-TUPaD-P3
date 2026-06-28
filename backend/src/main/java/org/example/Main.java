package org.example;

import org.example.entities.Categoria;
import org.example.entities.Producto;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProductoRepository;
import org.example.util.JPAUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final CategoriaRepository catRepo  = new CategoriaRepository();
    private static final ProductoRepository  prodRepo = new ProductoRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n==============================");
            System.out.println("       MENU PRINCIPAL         ");
            System.out.println("==============================");
            System.out.println("1. Gestionar Categorias");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Reportes");
            System.out.println("0. Salir");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuReportes();
                case 0 -> salir = true;
                default -> System.out.println("Opcion invalida. Intente nuevamente.");
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

        if (opt.isEmpty()) {
            System.out.println("Error: no existe ninguna categoria con ID " + id + ".");
            return;
        }
        if (opt.get().isEliminado()) {
            System.out.println("Error: la categoria ya estaba dada de baja.");
            return;
        }

        String nombre = opt.get().getNombre();
        catRepo.eliminarLogico(id);
        System.out.println("Categoria '" + nombre + "' dada de baja correctamente.");
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
        if (!nuevoNombre.isEmpty()) {
            cat.setNombre(nuevoNombre);
        }

        System.out.println("Descripcion actual: " + cat.getDescripcion());
        System.out.print("Nueva descripcion (Enter para mantener): ");
        String nuevaDesc = scanner.nextLine().trim();
        if (!nuevaDesc.isEmpty()) {
            cat.setDescripcion(nuevaDesc);
        }

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
                System.out.printf(" %-5d| %-21s| %s%n",
                        c.getId(), c.getNombre(), c.getDescripcion());
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

        System.out.println("Categorias disponibles:");
        System.out.println(" ID   | Nombre");
        System.out.println("------|----------------------");
        for (Categoria c : cats) {
            System.out.printf(" %-5d| %s%n", c.getId(), c.getNombre());
        }

        Long catId = leerLong("Seleccione ID de categoria: ");
        Optional<Categoria> catOpt = cats.stream()
                .filter(c -> c.getId().equals(catId))
                .findFirst();
        if (catOpt.isEmpty()) {
            System.out.println("Error: ID de categoria invalido.");
            return;
        }

        String nombre      = leerTextoObligatorio("Nombre del producto: ");
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();
        double precio      = leerDoublePositivo("Precio (mayor a 0): ");
        int    stock       = leerIntNoNegativo("Stock (mayor o igual a 0): ");

        Producto prod = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stock)
                .imagen("")
                .disponible(true)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .categoria(catOpt.get())
                .build();

        Producto guardado = prodRepo.guardar(prod);
        System.out.println("Producto creado con ID: " + guardado.getId()
                + ", categoria: " + catOpt.get().getNombre());
    }

    private static void bajaProducto() {
        System.out.println("\n-- Baja de Producto --");
        List<Producto> activos = listarProductos();
        if (activos.isEmpty()) return;

        Long id = leerLong("ID del producto a dar de baja: ");
        Optional<Producto> opt = prodRepo.buscarPorId(id);

        if (opt.isEmpty()) {
            System.out.println("Error: no existe ningun producto con ID " + id + ".");
            return;
        }
        if (opt.get().isEliminado()) {
            System.out.println("Error: el producto ya estaba dado de baja.");
            return;
        }

        String nombre = opt.get().getNombre();
        prodRepo.eliminarLogico(id);
        System.out.println("Producto '" + nombre + "' dado de baja correctamente.");
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
        if (!nuevoNombre.isEmpty()) {
            prod.setNombre(nuevoNombre);
        }

        System.out.printf("Precio actual: $%.2f%n", prod.getPrecio());
        System.out.print("Nuevo precio (Enter para mantener): ");
        String precioStr = scanner.nextLine().trim();
        if (!precioStr.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(precioStr);
                if (nuevoPrecio <= 0) {
                    System.out.println("El precio debe ser mayor a 0. Se mantiene el valor anterior.");
                } else {
                    prod.setPrecio(nuevoPrecio);
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Se mantiene el precio anterior.");
            }
        }

        System.out.println("Stock actual: " + prod.getStock());
        System.out.print("Nuevo stock (Enter para mantener): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(stockStr);
                if (nuevoStock < 0) {
                    System.out.println("El stock no puede ser negativo. Se mantiene el valor anterior.");
                } else {
                    prod.setStock(nuevoStock);
                }
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
            System.out.println("\n ID   | Nombre               | Precio     | Stock | Categoria");
            System.out.println("------|----------------------|------------|-------|--------------------");
            for (Producto p : activos) {
                String cat = p.getCategoria() != null ? p.getCategoria().getNombre() : "(sin categoria)";
                System.out.printf(" %-5d| %-21s| $%-10.2f| %-6d| %s%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), cat);
            }
        }
        return activos;
    }

    // ─────────────────────────────────────────────────────────────────
    // REPORTES
    // ─────────────────────────────────────────────────────────────────

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Reportes ---");
            System.out.println("1. Productos por categoria");
            System.out.println("0. Volver");
            int op = leerInt("Opcion: ");
            switch (op) {
                case 1 -> productosPorCategoria();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void productosPorCategoria() {
        System.out.println("\n-- Productos por Categoria --");
        List<Categoria> activas = listarCategorias();
        if (activas.isEmpty()) return;

        Long id = leerLong("Seleccione ID de categoria: ");
        Optional<Categoria> catOpt = activas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (catOpt.isEmpty()) {
            System.out.println("Error: ID de categoria invalido.");
            return;
        }

        List<Producto> productos = prodRepo.buscarPorCategoria(id);
        if (productos.isEmpty()) {
            System.out.println("La categoria '" + catOpt.get().getNombre()
                    + "' no tiene productos activos.");
        } else {
            System.out.println("\nProductos en '" + catOpt.get().getNombre() + "':");
            System.out.println(" ID   | Nombre               | Precio     | Stock");
            System.out.println("------|----------------------|------------|------");
            for (Producto p : productos) {
                System.out.printf(" %-5d| %-21s| $%-10.2f| %d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
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
            System.out.println("Este campo es obligatorio. Ingrese un valor.");
        }
    }

    private static double leerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val > 0) return val;
                System.out.println("El precio debe ser mayor a 0.");
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
                System.out.println("El stock no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }
}
