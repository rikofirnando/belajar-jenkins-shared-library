package cilestri.jenkins

class Output {

    static hello(steps, String name) {
        steps.echo "Hello ${name}!"
    }

}
