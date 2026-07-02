package org.example.repository;

import org.example.model.Producto;


public class ProductoRepository extends BaseRepository<Producto> {

    public ProductoRepository() {
        super(Producto.class);
    }

}
