import com.example.Note

class BootStrap {

    def init = { servletContext ->
        environments {
            test {
                new Note(title: 'Hello, World!', body: 'Hello from the Integration Test').save()
            }
        }
    }
    def destroy = {
    }
}
