async function cenaCompleta(){
    try {
        const pizza = await pedirPizza();
        console.log("Pedido.");
        
    }catch (error){
        console.error("Error al pedir la pizza:", error);
    }
}

async function pedirPizza(){
    setTimeout(() =>
        console.log("Pizza pedida!"), 2000
    )
}

cenaCompleta();